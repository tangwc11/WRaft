package com.wentry.wraft.transport.initializer;

import com.wentry.wraft.transport.codec.PacketCodec;
import com.wentry.wraft.transport.handler.AppendLogCmtHandler;
import com.wentry.wraft.transport.handler.AppendLogReqHandler;
import com.wentry.wraft.transport.handler.ClusterConnectHandler;
import com.wentry.wraft.transport.handler.HeartBeatHandler;
import com.wentry.wraft.transport.handler.LeaderChangePacketHandler;
import com.wentry.wraft.transport.handler.RedirectCmdHandler;
import com.wentry.wraft.transport.handler.ShutDownPacketHandler;
import com.wentry.wraft.transport.handler.StartPacketHandler;
import com.wentry.wraft.transport.handler.SyncAllDataReqHandler;
import com.wentry.wraft.transport.handler.VoteReqHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Description:
 * @Author: tangwc
 *
 * 服务端连接初始化，接受请求，如：同步集群、开启服务、投票、心跳
 *
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
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
        ch.pipeline().addLast(new ClusterConnectHandler());
        ch.pipeline().addLast(new StartPacketHandler());
        ch.pipeline().addLast(new VoteReqHandler());
        ch.pipeline().addLast(new HeartBeatHandler());
        ch.pipeline().addLast(new LeaderChangePacketHandler());
        ch.pipeline().addLast(new ShutDownPacketHandler());
        ch.pipeline().addLast(new RedirectCmdHandler());
        ch.pipeline().addLast(new SyncAllDataReqHandler());
        ch.pipeline().addLast(new AppendLogReqHandler());
        ch.pipeline().addLast(new AppendLogCmtHandler());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
