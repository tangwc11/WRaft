package com.wentry.wraft.storage;


import com.alibaba.fastjson2.JSONObject;
import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.storage.impl.MemoryStorage;
import com.wentry.wraft.transport.packet.AppendLogAckPacket;
import com.wentry.wraft.transport.packet.AppendLogCmtPacket;
import com.wentry.wraft.transport.packet.AppendLogReqPacket;
import com.wentry.wraft.transport.packet.SyncAllDataReqPacket;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * 数据仓库，存储k-v，可以后期拓展做持久化
 */
public class StorageManager {

    private static final Logger log = LoggerFactory.getLogger(StorageManager.class);

    private static final IStorage memoryStorage = new MemoryStorage();
    private static IStorage dbStorage = null;
    private static final Deque<CmdLog> committingLogQueue = new LinkedBlockingDeque<>();
    private static long emptyRound = 1;
    private static final AtomicReference<String> lastCmtLogId = new AtomicReference<>(UUID.randomUUID().toString());
    private static Thread scheduleThread;

    static {
        Executors.newFixedThreadPool(1).submit(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchLogToAppend();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static void fetchLogToAppend() throws InterruptedException {
        while (true) {
            CmdLog cmdLog = committingLogQueue.peekFirst();
//            log.debug("fetchLogToAppend, peekFirst cmdLog :{}", JSONObject.toJSONString(cmdLog));
            if (cmdLog == null) {
                //递增轮训
                Thread.sleep(Math.min(emptyRound++ * 50, 1000));
                continue;
            }

            if (commitIfSingleNodeCluster()) {
                //单节点集群，直接提交
                continue;
            }

            emptyRound = 1;
            ClusterManager.broadCast(
                    new AppendLogReqPacket()
                            .setReqId(UUID.randomUUID().toString())
                            .setCmdLog(cmdLog)
                            .setFrom(ClusterManager.localId())
                            .setLastCmtLogId(lastCmtLogId.get())
            );

            scheduleThread = Thread.currentThread();
            //阻塞这个线程，异步解除阻塞，3秒后重试
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        }
    }

    private static boolean commitIfSingleNodeCluster() {
        if (ClusterManager.nodes.size() == 0) {
            commitLog(committingLogQueue.pollFirst());
            return true;
        }
        return false;
    }

    public static void appendLog(String key, String val) {
        CmdLog cmdLog = new CmdLog().setLogId(UUID.randomUUID().toString()).setKey(key).setVal(val);
        committingLogQueue.addLast(cmdLog);
        log.debug("committingLogQueue :{}", JSONObject.toJSONString(committingLogQueue));
    }

    public static String getLastCmtLogId() {
        return lastCmtLogId.get();
    }

    public static Map<String, String> getAllData() {
        return dynamicGetStorage().getAllData();
    }

    static{
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                correctDbType();
                flushDB();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 一个rocksdb资源只允许一个写进程，但是网络分区会有两个leader，这里需要主动让渡出写权限
     * 1。所有的leader需要定期主动让渡出写权限（防止分区导致的隔离或者死锁）
     * 2。dbStorage先计入缓存，再由定时任务尝试刷盘定期刷盘
     * 副作用就是，刷盘前的数据 是不安全的。
     *
     * 更好一点的处理就是在rocksdb加一层代理，允许多个写进程同时打开
     */
    private static void flushDB() {

    }

    private static void correctDbType() {

    }

    public static void injectDbStorage(IStorage dbStorage) {
        StorageManager.dbStorage = dbStorage;
    }

    public static IStorage dynamicGetStorage() {
        if (StateManager.isLeader()) {
            return dbStorage;
        }
        return memoryStorage;
    }

    public static void syncAllData(Map<String, String> allData, String syncAllDataLastCmtLogId) {
        dynamicGetStorage().syncAllData(allData);
        lastCmtLogId.set(syncAllDataLastCmtLogId);
    }

    static Map<String, AtomicInteger> ackCount = new ConcurrentHashMap<>();

    public static void ackAppendLog(AppendLogAckPacket msg) {
        AtomicInteger count = ackCount.computeIfAbsent(msg.getReqId(), s -> new AtomicInteger(1));
        if (count.incrementAndGet() >= ClusterManager.nodes.size() / 2 + 1) {
            //已过半，提交
            CmdLog cmdLog = committingLogQueue.peekFirst();
            if (cmdLog == null) {
                return;
            }
            if (!cmdLog.getLogId().equals(msg.getCmdLogId())) {
                log.warn("first committing log is not equal with msg");
                return;
            }
            commitLog(committingLogQueue.pollFirst());
            //解除阻塞的线程
            if (scheduleThread != null) {
                LockSupport.unpark(scheduleThread);
            }
        } else {
            //未过半，继续等待ack
            log.debug("cmdLog :{} ack process ack:{},total:{}", msg.getCmdLogId(), count.get(), ClusterManager.nodes.size());
        }
    }

    private static void commitLog(CmdLog cmtLog) {
        if (cmtLog == null) {
            return;
        }
        ClusterManager.broadCast(
                new AppendLogCmtPacket()
                        .setCmdLog(cmtLog)
                        .setLastCmtLogId(lastCmtLogId.get())
                        .setCmtLogId(cmtLog.getLogId())
        );
        commitLocalLog(cmtLog);
    }

    public static void commitLocalLog(CmdLog cmtLog) {
        dynamicGetStorage().set(cmtLog.getKey(), cmtLog.getVal());
        lastCmtLogId.set(cmtLog.getLogId());
    }

    public static void reqSyncAllData() {
        Channel leaderChannel = ClusterManager.getLeaderChannel();
        if (leaderChannel != null) {
            leaderChannel.writeAndFlush(new SyncAllDataReqPacket().setReqPeerId(ClusterManager.localId()));
        }
    }

    public static String get(String key) {
        return dynamicGetStorage().get(key);
    }

    public static void closeResources() {
        if (dbStorage != null) {
            dbStorage.close();
            dbStorage = null;
        }
    }
}
