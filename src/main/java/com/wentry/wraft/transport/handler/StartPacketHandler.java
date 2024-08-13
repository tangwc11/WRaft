package com.wentry.wraft.transport.handler;

import com.alibaba.fastjson2.JSONObject;
import com.wentry.wraft.core.NodeStats;
import com.wentry.wraft.core.Scheduler;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.transport.packet.StartPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class StartPacketHandler extends SimpleChannelInboundHandler<StartPacket> {

    private static final Logger log = LoggerFactory.getLogger(StartPacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StartPacket msg) {
        log.info("receive msg:{}", JSONObject.toJSONString(msg));
        if (StateManager.compareAndSet(NodeStats.INITIAL, NodeStats.FOLLOWER)) {
            log.debug("start electionCountdown");
            Scheduler.getInstance().electionCountdown();
            return;
        }
        log.debug("start electionCountdown failed");
    }

}
