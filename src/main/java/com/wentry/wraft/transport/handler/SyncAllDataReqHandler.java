package com.wentry.wraft.transport.handler;

import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.SyncAllDataReqPacket;
import com.wentry.wraft.transport.packet.SyncAllDataRespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class SyncAllDataReqHandler extends SimpleChannelInboundHandler<SyncAllDataReqPacket> {
    private static final Logger log = LoggerFactory.getLogger(SyncAllDataReqHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SyncAllDataReqPacket msg) throws Exception {
        log.info("received msg:{}", msg);

        String logId = StorageManager.getLastCmtLogId();
        SyncAllDataRespPacket syncAllDataRespPacket = new SyncAllDataRespPacket()
                .setData(StorageManager.getAllData())
                .setReqPeerId(msg.getReqPeerId())
                .setLastCmtLogId(StorageManager.getLastCmtLogId());

        //防止lastCmtLogId和data不一致，这里自璇一下
        while (!logId.equals(StorageManager.getLastCmtLogId())) {
            logId = StorageManager.getLastCmtLogId();
            syncAllDataRespPacket = new SyncAllDataRespPacket()
                    .setData(StorageManager.getAllData())
                    .setReqPeerId(msg.getReqPeerId())
                    .setLastCmtLogId(StorageManager.getLastCmtLogId());
        }

        ctx.channel().writeAndFlush(syncAllDataRespPacket);
    }
}
