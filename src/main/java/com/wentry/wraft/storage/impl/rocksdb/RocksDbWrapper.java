package com.wentry.wraft.storage.impl.rocksdb;

import com.wentry.wraft.storage.CmdLog;
import com.wentry.wraft.storage.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: tangwc
 */
public class RocksDbWrapper implements IStorage {

    private static final Logger log = LoggerFactory.getLogger(RocksDbWrapper.class);

    private RocksDbStorage rocksDbStorage;
    private final Deque<CmdLog> buffer = new LinkedBlockingDeque<>();

    private boolean readonly = true;
    private final String dir;

    public RocksDbWrapper(String dir) {
        //默认开启的是readonly
        this.rocksDbStorage = new RocksDbStorage(dir, true);
        this.dir = dir;
        //开启定时写权限让渡任务
        //开启定时刷盘任务
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                this::releaseWriteOrFlush,
                1,
                1,
                TimeUnit.SECONDS);
    }

    private void releaseWriteOrFlush() {
        if (needRelease()) {
            doRelease();
        } else {
            tryFlush();
        }
    }

    private void tryFlush() {
        if (CollectionUtils.isEmpty(buffer)) {
            return;
        }
        if (!this.readonly) {
            doFlush();
            return;
        }
        trySwitchWrite();
    }

    private void trySwitchWrite() {
        RocksDbStorage db = new RocksDbStorage(dir, false);
        if (db.isReady()) {
            this.rocksDbStorage.close();
            this.rocksDbStorage = db;
            this.readonly = false;
            doFlush();
        }
    }

    private void doFlush() {
        boolean flush = false;
        while (!buffer.isEmpty()) {
            CmdLog cmdLog = buffer.pollFirst();
            this.rocksDbStorage.set(cmdLog.getKey(), cmdLog.getVal());
            log.info("flush log to disk :{}", cmdLog);
            flush = true;
        }
        if (flush) {
            lastFlushTime = System.currentTimeMillis();
        }
    }

    private void doRelease() {
        this.rocksDbStorage.close();
        this.rocksDbStorage = new RocksDbStorage(dir, true);
        this.readonly = true;
    }

    private boolean needRelease() {
        if (readonly) {
            //只读，不需要让渡
            return false;
        }
        return CollectionUtils.isEmpty(buffer) && noneWritePast5Second();
    }

    long lastFlushTime = 0;

    //5之内没写入
    private boolean noneWritePast5Second() {
        return lastFlushTime < System.currentTimeMillis() - 5 * 1000L;
    }

    @Override
    public void set(String key, String val) {
        buffer.add(new CmdLog().setKey(key).setVal(val));
//        rocksDbStorage.set(key, val);
    }

    @Override
    public String get(String key) {
        return rocksDbStorage.get(key);
    }

    @Override
    public Map<String, String> getAllData() {
        return rocksDbStorage.getAllData();
    }

    @Override
    public void syncAllData(Map<String, String> allData) {
        rocksDbStorage.syncAllData(allData);
    }

    @Override
    public void close() {
        rocksDbStorage.close();
    }
}
