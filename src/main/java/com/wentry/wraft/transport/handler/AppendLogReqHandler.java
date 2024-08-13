package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.AppendLogAckPacket;
import com.wentry.wraft.transport.packet.AppendLogReqPacket;
import com.wentry.wraft.transport.packet.SyncAllDataReqPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class AppendLogReqHandler extends SimpleChannelInboundHandler<AppendLogReqPacket> {

    private static final Logger log = LoggerFactory.getLogger(AppendLogReqHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AppendLogReqPacket msg) throws Exception {
        log.info("received msg:{}", msg);

        if (!msg.getLastCmtLogId().equals(StorageManager.getLastCmtLogId())) {
            log.info("ignore for lastCmtLog not equal, local is:{}, leader is:{}", StorageManager.getLastCmtLogId(), msg.getLastCmtLogId());
            //请求同步全量数据
            ctx.writeAndFlush(new SyncAllDataReqPacket().setReqPeerId(ClusterManager.localId()));
            return;
        }

        ctx.channel().writeAndFlush(new AppendLogAckPacket()
                .setAckPeerId(ClusterManager.localId())
                .setReqId(msg.getReqId())
                .setCmdLogId(msg.getCmdLog().getLogId())
        );

    }
}
