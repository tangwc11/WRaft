package com.wentry.wraft.transport;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.transport.initializer.ServerChannelInitializer;
import com.wentry.wraft.spring.config.WRaftConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器，专门用一个线程启动
 */
public class NettyServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Channel localChannel;
    private EventLoopGroup group;

    @Override
    public void run() {
        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() throws InterruptedException {
        int port = WRaftConfig.getInstance().getPort();
        log.debug("server will start with host:{}, port:{}", WRaftConfig.getInstance().getHost(), port);
        this.group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture future = bootstrap.group(this.group)
                    .localAddress(port)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .bind()
                    .sync();

            log.info("server started with host:{}, port:{}", WRaftConfig.getInstance().getHost(), port);
            localChannel = future.channel();
            future.channel().closeFuture().sync();//阻塞等待主channel关闭
        } finally {
            this.group.shutdownGracefully();
        }
    }

    public void doElection() {

    }

    public void destroy() throws Exception {
        if (this.localChannel != null) {
            this.localChannel.close();
        }
        if (this.group != null) {
            this.group.shutdownGracefully();
        }

        //client的channel也关闭下
        ClusterManager.destroy();
    }
}
