package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步集群信息
 * 1。有哪些节点
 * 2。当前的主节点是谁，若有的话
 */
@WRaftPacket
@ToString
public class ClusterConnectPacket extends BasePacket{

    /**
     * host:port格式填充的节点列表
     */
    private List<String> nodeIds = new ArrayList<>();
    private Map<String, Integer> nodeIdMapHttpPort = new ConcurrentHashMap<>();
    private String reqId;
    private String reqHost;
    private int reqPort;

    public String getReqHost() {
        return reqHost;
    }

    public ClusterConnectPacket setReqHost(String reqHost) {
        this.reqHost = reqHost;
        return this;
    }

    public Map<String, Integer> getNodeIdMapHttpPort() {
        return nodeIdMapHttpPort;
    }

    public ClusterConnectPacket setNodeIdMapHttpPort(Map<String, Integer> nodeIdMapHttpPort) {
        this.nodeIdMapHttpPort = nodeIdMapHttpPort;
        return this;
    }

    public int getReqPort() {
        return reqPort;
    }

    public ClusterConnectPacket setReqPort(int reqPort) {
        this.reqPort = reqPort;
        return this;
    }

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public ClusterConnectPacket setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
        return this;
    }

    @Override
    public int type() {
        return CLUSTER_CONNECT;
    }

    public String getReqId() {
        return reqId;
    }

    public ClusterConnectPacket setReqId(String reqId) {
        this.reqId = reqId;
        return this;
    }
}
