package com.wentry.wraft;

import com.wentry.wraft.transport.NettyServer;
import com.wentry.wraft.spring.config.WRaftConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 启动类
 * 1。 启动web服务器，用于人类交互
 * 2。 启动netty服务器，用于节点间通信
 */
@SpringBootApplication
@EnableConfigurationProperties(WRaftConfig.class)
public class Application implements DisposableBean {

    private static NettyServer server;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //启动节点
        new Thread((server = new NettyServer())).start();
    }

    @Override
    public void destroy() throws Exception {
        if (server != null) {
            server.destroy();
        }
    }
}
