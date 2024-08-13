package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.AppendLogCmtPacket;
import com.wentry.wraft.transport.packet.SyncAllDataReqPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class AppendLogCmtHandler extends SimpleChannelInboundHandler<AppendLogCmtPacket> {
    private static final Logger log = LoggerFactory.getLogger(AppendLogCmtHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AppendLogCmtPacket msg) throws Exception {
        log.info("received msg :{}", msg);

        if (!StorageManager.getLastCmtLogId().equals(msg.getLastCmtLogId())) {
            log.info("return for not equal with cmt packet last cmtLogId, local :{}, msg:{}",
                    StorageManager.getLastCmtLogId(), msg.getCmtLogId());
            //同步全量数据
            ctx.writeAndFlush(new SyncAllDataReqPacket().setReqPeerId(ClusterManager.localId()));
            return;
        }

        StorageManager.commitLocalLog(msg.getCmdLog());
    }
}
