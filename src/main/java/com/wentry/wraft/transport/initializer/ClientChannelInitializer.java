package com.wentry.wraft.transport.initializer;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.transport.codec.PacketCodec;
import com.wentry.wraft.transport.handler.AppendLogAckHandler;
import com.wentry.wraft.transport.handler.ClusterConnectHandler;
import com.wentry.wraft.transport.handler.HeartBeatRespHandler;
import com.wentry.wraft.transport.handler.ShutDownPacketHandler;
import com.wentry.wraft.transport.handler.SyncAllDataRespHandler;
import com.wentry.wraft.transport.handler.VoteRespHandler;
import com.wentry.wraft.transport.packet.ShutDownPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Description:
 * @Author: tangwc
 * 客户端连接初始化
 * 处理主动发请求之后的响应，如：投票响应、心跳响应
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // 添加帧解码器
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE, // 最大帧长度，可以根据实际情况调整
                4,     // 长度字段偏移量
                4,     // 长度字段长度
                0,     // 长度调整值
                0));   // 初始偏移量
        //编解码
        ch.pipeline().addLast(new PacketCodec());
        //同步集群
        ch.pipeline().addLast(new ClusterConnectHandler());
        ch.pipeline().addLast(new VoteRespHandler());
        ch.pipeline().addLast(new HeartBeatRespHandler());
        ch.pipeline().addLast(new ShutDownPacketHandler());
        ch.pipeline().addLast(new AppendLogAckHandler());
        ch.pipeline().addLast(new SyncAllDataRespHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().writeAndFlush(new ShutDownPacket().setShutDownIp(ClusterManager.localId()));
    }
}
