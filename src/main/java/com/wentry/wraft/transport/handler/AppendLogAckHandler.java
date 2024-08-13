package com.wentry.wraft.transport.handler;

import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.AppendLogAckPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class AppendLogAckHandler extends SimpleChannelInboundHandler<AppendLogAckPacket> {

    private static final Logger log = LoggerFactory.getLogger(AppendLogAckHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AppendLogAckPacket msg) throws Exception {
        log.info("received msg:{}", msg);

        StorageManager.ackAppendLog(msg);

    }
}
