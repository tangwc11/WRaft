package com.wentry.wraft.storage;

import java.util.Map;

/**
 * @Description:
 * @Author: tangwc
 */
public interface IStorage {

    /**
     * 包含add、update、delete语义
     * 返回旧值
     */
    void set(String key,String val);

    String get(String key);

    Map<String, String> getAllData();

    void syncAllData(Map<String, String> allData);

    /**
     * 关闭资源
     */
    void close();
}
