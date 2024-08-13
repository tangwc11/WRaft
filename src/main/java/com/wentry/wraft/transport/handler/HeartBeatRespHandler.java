package com.wentry.wraft.transport.handler;

import com.wentry.wraft.transport.packet.HeartBeatRespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳响应
 */
public class HeartBeatRespHandler extends SimpleChannelInboundHandler<HeartBeatRespPacket> {


    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRespPacket msg) throws Exception {
        log.info("receive msg :{}", msg);
    }
}
