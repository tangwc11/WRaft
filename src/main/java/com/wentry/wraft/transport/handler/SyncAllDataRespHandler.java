package com.wentry.wraft.transport.handler;

import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.SyncAllDataRespPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Description:
 * @Author: tangwc
 */
public class SyncAllDataRespHandler extends SimpleChannelInboundHandler<SyncAllDataRespPacket> {

    private static final Logger log = LoggerFactory.getLogger(SyncAllDataRespHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SyncAllDataRespPacket msg) throws Exception {
        log.info("received msg:{}", msg);

        Map<String, String> allData = msg.getData();
        String lastCmtLogId = msg.getLastCmtLogId();
        StorageManager.syncAllData(allData, lastCmtLogId);
    }
}
