package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.StateManager;
import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.RedirectPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: tangwc
 */
public class RedirectCmdHandler extends SimpleChannelInboundHandler<RedirectPacket> {

    private static final Logger log = LoggerFactory.getLogger(RedirectCmdHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedirectPacket msg) throws Exception {

        log.info("received msg :{}", msg);

        if (!StateManager.isLeader()) {
            log.debug("ignore for curr node is not leader any more, curr:{}", StateManager.currState());
            return;
        }

        StorageManager.appendLog(msg.getKey(), msg.getVal());
    }
}
