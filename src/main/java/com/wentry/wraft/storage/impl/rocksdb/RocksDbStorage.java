package com.wentry.wraft.storage.impl.rocksdb;

import com.wentry.wraft.storage.IStorage;
import com.wentry.wraft.util.SerializationUtils;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: tangwc
 */
public class RocksDbStorage implements IStorage {

    private static final Logger log = LoggerFactory.getLogger(RocksDbStorage.class);

    private RocksDB db;
    private String dir;
    private boolean ready;

    public boolean isReady() {
        return ready;
    }

    public RocksDbStorage(String dir, boolean readonly) {
        this.dir = dir;
        initialize(readonly);
    }

    boolean retry = true;
    private void initialize(boolean readonly) {
        RocksDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);
        String fileName = "wraft-db";
        File baseDir = new File(dir, fileName);
        try {
            Files.createDirectories(baseDir.getParentFile().toPath());
            Files.createDirectories(baseDir.getAbsoluteFile().toPath());
            log.info("RocksDB begin init, db file is :{}", baseDir.getAbsolutePath() + "/" + fileName);
            //同一个资源，只允许一个写进程
            if (readonly) {
                db = RocksDB.openReadOnly(options, baseDir.getAbsolutePath());
            }else{
                db = RocksDB.open(options, baseDir.getAbsolutePath());
            }
            log.info("RocksDB initialized, db file is :{}", baseDir.getAbsolutePath() + "/" + fileName);
            this.ready = true;
        } catch (IOException | RocksDBException e) {
            log.error("Error initializing RocksDB. Exception: '{}', message: '{}'", e.getCause(), e.getMessage(), e);
            if (readonly && retry) {
                retryInit(readonly, options, baseDir, e);
            }
        }
    }

    private void retryInit(boolean readonly, Options options, File baseDir, Exception e) {
        try {
            db = RocksDB.open(options, baseDir.getAbsolutePath());
        } catch (RocksDBException ex) {
            log.error("Error retry initializing RocksDB. Exception: '{}', message: '{}'", e.getCause(), e.getMessage(), e);
        }
        retry = false;
        if (db != null) {
            db.close();
            db = null;
        }
        initialize(readonly);
    }


    @Override
    public void set(String key, String val) {
        try {
            db.put(key.getBytes(), SerializationUtils.serialize(val));
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        String value = null;
        try {
            byte[] bytes = db.get(key.getBytes());
            if (bytes != null) {
                value = SerializationUtils.deserialize(bytes, String.class);
            }
        } catch (RocksDBException e) {
            log.error(
                    "Error retrieving the entry with key: {}, cause: {}, message: {}",
                    key,
                    e.getCause(),
                    e.getMessage()
            );
        }
        log.info("finding key '{}' returns '{}'", key, value);
        return value;
    }

    @Override
    public Map<String, String> getAllData() {
        Map<String, String> allData = new ConcurrentHashMap<>();
        try (RocksIterator iterator = db.newIterator()) {
            // 设置读取选项
            ReadOptions readOptions = new ReadOptions();
            readOptions.setFillCache(false); // 根据需要设置

            // 初始化迭代器
            iterator.seekToFirst(); // 移动到第一个元素

            while (iterator.isValid()) {
                // 获取当前的键值对
                byte[] key = iterator.key();
                byte[] bytes = iterator.value();

                String val = SerializationUtils.deserialize(bytes, String.class);
                allData.put(new String(key), val);
                // 移动到下一个元素
                iterator.next();
            }
        }
        return allData;
    }

    @Override
    public void syncAllData(Map<String, String> allData) {
        //empty implementation
    }

    @Override
    public void close() {
        if (db != null) {
            db.close();
        }
    }
}
