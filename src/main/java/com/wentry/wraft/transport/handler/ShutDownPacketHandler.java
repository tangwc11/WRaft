package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.transport.packet.ShutDownPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class ShutDownPacketHandler extends SimpleChannelInboundHandler<ShutDownPacket> {

    private static final Logger log = LoggerFactory.getLogger(ShutDownPacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ShutDownPacket msg) throws Exception {
        log.info("received msg :{}", msg);

        //剔除下线的机器
        String shutDownIp = msg.getShutDownIp();
        ClusterManager.remove(shutDownIp);
    }
}
