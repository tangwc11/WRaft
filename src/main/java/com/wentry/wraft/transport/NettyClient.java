package com.wentry.wraft.transport;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.transport.initializer.ClientChannelInitializer;
import com.wentry.wraft.transport.packet.ClusterConnectPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 客户端，用于去主动连接别人，一个节点里面可能包含多个客户端
 */
public class NettyClient {

    private final EventLoopGroup group;

    public NettyClient(EventLoopGroup group) {
        this.group = group;
    }

    public Channel connect(String host, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer())
                .connect(new InetSocketAddress(host, port)).sync();

        //连接之后，立即给对方发送一个集群同步的请求，将本地的集群同步给对方；同样，对方响应完毕后，也会给己方同步集群
        future.channel().writeAndFlush(
                new ClusterConnectPacket()
                        .setNodeIds(ClusterManager.export())
                        .setNodeIdMapHttpPort(ClusterManager.getNodeIdMapHttpPort())
                        .setReqId(ClusterManager.localId())
        );
        return future.channel();
    }

}
