package com.wentry.wraft.transport.handler;

import com.alibaba.fastjson2.JSONObject;
import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.transport.packet.LeaderChangePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class LeaderChangePacketHandler extends SimpleChannelInboundHandler<LeaderChangePacket> {

    private static final Logger log = LoggerFactory.getLogger(LeaderChangePacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LeaderChangePacket msg) throws Exception {
        log.info("received msg:{}", JSONObject.toJSONString(msg));

        if (ClusterManager.localId().equals(msg.getNewLeaderId())) {
            log.info("ignore for new leader is curr node.");
            return;
        }

        if (ClusterManager.getLeader().equals(msg.getNewLeaderId())) {
            log.info("curr leader is already broadcast leader.");
            return;
        }

        log.info("set new leader and term from :{}:{}, to :{}",
                ClusterManager.getLeader(), StateManager.getTerm(), JSONObject.toJSONString(msg));
        ClusterManager.setLeader(msg.getNewLeaderId());
        StateManager.setTerm(msg.getNewTerm());

    }
}
