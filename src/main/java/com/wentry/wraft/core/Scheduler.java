package com.wentry.wraft.core;

import com.wentry.wraft.transport.packet.StartPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 调度者，负责
 * 1、定时任务
 * 2、产生和接受事件
 */
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private static final Scheduler instance = new Scheduler();
    private final ScheduledExecutorService executors = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> electionSchedule;
    private ScheduledFuture<?> heartBeatSchedule;
    private final int randomRangeMs = 300;
    private final int heartbeatBaseMs = 500;

    private Scheduler() {
    }

    public static Scheduler getInstance() {
        return instance;
    }

    public void start() {
        if (!StateManager.compareAndSet(NodeStats.INITIAL, NodeStats.FOLLOWER)) {
            log.debug("start fail for curr state:{}", StateManager.currState());
            return;
        }
        //开启服务，就是从开启选举倒计时开始
        electionCountdown();
        //所有节点发送start事件
        ClusterManager.nodes.forEach((s, channel) -> channel.writeAndFlush(new StartPacket()));
    }

    public void electionCountdown() {
        //每次开启，都重新计算一个新的随机时间
        long electionGapMs = (heartbeatBaseMs + randomRangeMs + new Random().nextInt(randomRangeMs));
        log.debug("schedule will exec every {} ms", electionGapMs);
        if (this.electionSchedule != null && !this.electionSchedule.isCancelled()) {
            this.electionSchedule.cancel(true);
        }
        //选举时，需要停止心跳
        if (heartBeatSchedule != null && !heartBeatSchedule.isCancelled()) {
            this.heartBeatSchedule.cancel(true);
        }
        this.electionSchedule = executors.scheduleAtFixedRate(
                ElectionManager::doElection, electionGapMs, electionGapMs, TimeUnit.MILLISECONDS
        );
    }

    public void heartBeatCountdown(int term) {
        long heartBeatGapMs = (heartbeatBaseMs + new Random().nextInt(randomRangeMs));
        log.debug("heartbeat will exec every {} ms", heartBeatGapMs);
        //变更为leader，需要停掉自身的选举倒计时
        if (this.electionSchedule != null && !this.electionSchedule.isCancelled()) {
            this.electionSchedule.cancel(true);
        }
        StateManager.setTerm(term);
        if (heartBeatSchedule != null && !heartBeatSchedule.isCancelled()) {
            this.heartBeatSchedule.cancel(true);
        }
        heartBeatSchedule = executors.scheduleAtFixedRate(
                ClusterManager::doHeartBeat, 0, heartBeatGapMs, TimeUnit.MILLISECONDS
        );
    }
}
