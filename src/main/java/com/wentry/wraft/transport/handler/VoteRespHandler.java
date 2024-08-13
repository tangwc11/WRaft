package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ElectionManager;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.transport.packet.VoteRespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 投票响应处理器
 */
public class VoteRespHandler extends SimpleChannelInboundHandler<VoteRespPacket> {

    private static final Logger log = LoggerFactory.getLogger(VoteRespHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VoteRespPacket msg) throws Exception {
        log.info("received msg:{}", msg);

        if (StateManager.isLeader() || StateManager.isFollower()) {
            log.info("leader or follower ignore vote resp");
            return;
        }
        log.debug("do rcvVote for msg:{}", msg);
        ElectionManager.rcvVote(msg.getTerm());
    }
}
