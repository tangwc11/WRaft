package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.core.NodeStats;
import com.wentry.wraft.core.Scheduler;
import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.HeartBeatPacket;
import com.wentry.wraft.transport.packet.HeartBeatRespPacket;
import com.wentry.wraft.transport.packet.LeaderChangePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应心跳
 * 1. 当前是 follower，响应大于等于currTerm的心跳
 * 2. 当前是 leader，
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<HeartBeatPacket> {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatPacket msg) throws Exception {
        log.info("receive msg:{}", msg);

        if (StateManager.getTerm().get() > msg.getCurrTerm() && StateManager.isLeader()) {
            //高任期leader不响应心跳
            log.debug("higher term leader ignore heartbeat");
            return;
        }

        if (StateManager.getTerm().get() <= msg.getCurrTerm()) {
            //低任期的任何情况，都会响应心跳
            log.info("accept new term, curr:{}", StateManager.getTerm());
            acceptNewTerm(ctx, msg);
        }

        //其余的情况，不再响应心跳
    }

    /**
     * 完全接受新leader
     */
    private void acceptNewTerm(ChannelHandlerContext ctx, HeartBeatPacket msg) {
        StateManager.setTerm(msg.getCurrTerm());
        ClusterManager.setLeader(msg.getLeaderId());
        if (StateManager.isFollower()) {
            //follower直接重置选举
            Scheduler.getInstance().electionCountdown();
        } else if (StateManager.isCandidate()) {
            //candidate do nothing
        } else if (StateManager.isLeader()) {
            //leader 变更状态，周知变更，开启选举
            StateManager.compareAndSet(NodeStats.LEADER, NodeStats.FOLLOWER);
            ClusterManager.setLeader(msg.getLeaderId());
            //另一个leader的数据全部丢弃，因为不会有新提交的数据
            StorageManager.reqSyncAllData();
            ClusterManager.broadCast(new LeaderChangePacket()
                    .setNewTerm(msg.getCurrTerm())
                    .setPreLeaderId(ClusterManager.localId())
                    .setNewLeaderId(msg.getLeaderId())
            );
            Scheduler.getInstance().electionCountdown();
        } else if (StateManager.isInitial()) {
            StateManager.compareAndSet(NodeStats.INITIAL, NodeStats.FOLLOWER);
            StorageManager.reqSyncAllData();
            Scheduler.getInstance().electionCountdown();
        }
        ctx.channel().writeAndFlush(new HeartBeatRespPacket().setRespNodeId(ClusterManager.localId()));
    }
}
