package com.wentry.wraft.spring.config;

import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.storage.impl.rocksdb.RocksDbWrapper;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: tangwc
 */
@Configuration
public class WRaftConfiguration implements SmartInitializingSingleton {


    @Override
    public void afterSingletonsInstantiated() {
        StorageManager.injectDbStorage(new RocksDbWrapper("wraft-db"));
    }
}
