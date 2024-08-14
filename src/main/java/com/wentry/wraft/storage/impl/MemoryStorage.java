package com.wentry.wraft.storage.impl;

import com.wentry.wraft.storage.IStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: tangwc
 */
public class MemoryStorage implements IStorage {

    Map<String, String> data = new ConcurrentHashMap<>();
    @Override
    public void set(String key, String val) {
        data.put(key, val);
    }

    @Override
    public String get(String key) {
        return data.get(key);
    }

    @Override
    public Map<String, String> getAllData() {
        return data;
    }

    @Override
    public void syncAllData(Map<String, String> allData) {
        data = allData;
    }

    @Override
    public void close() {
        //do nothing
    }
}
