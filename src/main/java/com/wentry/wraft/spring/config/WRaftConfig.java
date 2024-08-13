package com.wentry.wraft.spring.config;

import com.wentry.wraft.util.IpUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置管理类，集合本地属性和spring配置
 */
@Configuration
@ConfigurationProperties(prefix = "wraft")
public class WRaftConfig implements SmartInitializingSingleton {

    static WRaftConfig instance;

    public static WRaftConfig getInstance(){
        return instance;
    }

    @Autowired
    Internal internal;

    @Autowired
    ServerProperties serverProperties;

    private String host;

    @Configuration
    @ConfigurationProperties("internal")
    static class Internal{
        int port;

        public int getPort() {
            return port;
        }
        public Internal setPort(int port) {
            this.port = port;
            return this;
        }

    }

    public int getPort() {
        return internal.port;
    }

    public int getHttpPort(){
        return serverProperties.getPort();
    }

    public Internal getInternal() {
        return internal;
    }

    public WRaftConfig setInternal(Internal internal) {
        this.internal = internal;
        return this;
    }

    public String getHost() {
        return host;
    }

    public WRaftConfig setHost(String host) {
        this.host = host;
        return this;
    }

    @Override
    public void afterSingletonsInstantiated() {
        setHost(IpUtils.getIpV4());
        instance = this;
    }
}
