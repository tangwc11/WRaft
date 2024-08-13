package com.wentry.wraft.transport.handler;

import com.wentry.wraft.core.ClusterManager;
import com.wentry.wraft.transport.packet.ClusterConnectPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 集群同步处理器
 */
@ChannelHandler.Sharable
public class ClusterConnectHandler extends SimpleChannelInboundHandler<ClusterConnectPacket> {

    private static final Logger log = LoggerFactory.getLogger(ClusterConnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterConnectPacket msg) throws Exception {
        log.debug("ClusterSyncHandler received msg:{}", msg);

        List<String> nodeIds = msg.getNodeIds();
        for (String nodeId : nodeIds) {
            if (ClusterManager.isSelf(nodeId)) {
                continue;
            }
            if (ClusterManager.alreadyEstablished(nodeId)) {
                log.debug("address already established for address:{}", nodeId);
                continue;
            }
            //新连接，并同步，最终构成两两相连接的集群peers
            Pair<String, Integer> hostPort = ClusterManager.split(nodeId);
            //没有加入的集群中，没有建立连接的节点，此时建立连接，并缓存channel
            Integer httpPort = msg.getNodeIdMapHttpPort().get(nodeId);
            ClusterManager.connect(hostPort.getLeft(), hostPort.getRight(), httpPort);
        }

    }
}
