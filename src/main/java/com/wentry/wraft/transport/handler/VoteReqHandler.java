package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.core.ElectionManager;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.transport.packet.VoteReqPacket;
import com.wentry.wraft.transport.packet.VoteRespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 投票请求处理器
 */
public class VoteReqHandler extends SimpleChannelInboundHandler<VoteReqPacket> {

    private static final Logger log = LoggerFactory.getLogger(VoteReqHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VoteReqPacket msg) throws Exception {
        log.info("received vote msg:{}", msg);

        if (StateManager.isLeader()) {
            //leader永远不会响应投票
            log.debug("leader ignore vote req");
            return;
        }

        if (ElectionManager.termVoted(msg.getTerm())) {
            //这个term已经投过票了，包含投自己的票
            log.info("term voted");
            return;
        }

        if (StateManager.getTerm().get() >= msg.getTerm()) {
            //当前任期大于等于拉票任期，不再响应拉票
            log.info("curr term ge vote term, curr:{}", StateManager.getTerm());
            return;
        }

        log.debug("vote for msg:{}", msg);
        //记录投票的term，响应拉票
        ElectionManager.recordVoteTerm(msg.getTerm());
        ctx.channel().writeAndFlush(
                new VoteRespPacket().setTerm(msg.getTerm()).setVoterId(ClusterManager.localId())
        );
    }
}
