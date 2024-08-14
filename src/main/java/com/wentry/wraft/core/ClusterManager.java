package com.wentry.wraft.core;

import com.alibaba.fastjson2.JSONObject;
import com.wentry.wraft.spring.config.WRaftConfig;
import com.wentry.wraft.transport.NettyClient;
import com.wentry.wraft.transport.packet.HeartBeatPacket;
import com.wentry.wraft.transport.packet.ShutDownPacket;
import com.wentry.wraft.util.HttpUtils;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 集群维护
 * 1. 维护所有本节点作为客户端的channel
 */
public class ClusterManager {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

    public static final Map<String, Channel> nodes = new ConcurrentHashMap<>();
    public static final NioEventLoopGroup nettyServerGroup = new NioEventLoopGroup();
    private static String leader = Strings.EMPTY;

    public static void addNode(String host, int port, Channel channel, int httpPort) {
        String nodeId = identity(host, port);
        log.debug("add node id:{},channel:{}", nodeId, channel);
        nodes.put(nodeId, channel);
        nodeIdMapHttpPort.put(nodeId, httpPort);
    }

    public static String identity(String host, int port) {
        return host + ":" + port;
    }

    public static List<String> export() {
        ArrayList<String> nodeIds = new ArrayList<>(nodes.keySet());
        nodeIds.add(localId());
        return nodeIds;
    }

    public static Map<String, Object> state() {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("current", ClusterManager.localId());
        state.put("state", StateManager.currState());
        state.put("term", StateManager.getTerm());
        state.put("cluster", new ArrayList<>(nodes.keySet()).stream().sorted().collect(Collectors.toList()));
        state.put("master", getLeader());
        state.put("data", getClusterDataSnapshot());
        return state;
    }

    private static Object getClusterDataSnapshot() {
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put(ClusterManager.localId(), HttpUtils.get("http://" + WRaftConfig.getInstance().getHost() + ":" + WRaftConfig.getInstance().getHttpPort() + "/w-raft/data/getAll"));
        for (Map.Entry<String, Channel> ety : ClusterManager.nodes.entrySet()) {
            String nodeId = ety.getKey();
            String[] split = ety.getKey().split(":");
            String ip = split[0];
            Integer httpPort = ClusterManager.nodeIdMapHttpPort.get(nodeId);
            String resp = HttpUtils.get("http://" + ip + ":" + httpPort + "/w-raft/data/getAll");
            if (resp == null) {
                continue;
            }
            data.put(nodeId, resp);
        }
        Map<String, String> finalData = new HashMap<>();
        for (Map.Entry<String, Object> ety : data.entrySet()) {
            String val = JSONObject.parse(String.valueOf(ety.getValue())).toJSONString();
            finalData.put(ety.getKey(), val);
        }
        return finalData;
    }

    public static String localId() {
        return identity(WRaftConfig.getInstance().getHost(), WRaftConfig.getInstance().getPort());
    }

    public static Pair<String, Integer> split(String identity) {
        String[] split = identity.split(":");
        return Pair.of(split[0], Integer.valueOf(split[1]));
    }

    public static boolean alreadyEstablished(String identity) {
        Channel channel = nodes.get(identity);
        if (channel != null && channel.isActive()) {
            return true;
        }
        return false;
    }

    public static void connect(String host, Integer port, int httpPort) throws InterruptedException {

        Channel channel = new NettyClient(nettyServerGroup).connect(host, port);
        if (channel == null) {
            log.error("channel connect failed. host:{},port:{}", host, port);
        }
        addNode(host, port, channel, httpPort);
    }

    public static void destroy() {
        for (Channel channel : nodes.values()) {
            if (channel.isActive()) {
                //通知下线
                channel.writeAndFlush(new ShutDownPacket().setShutDownIp(ClusterManager.localId()));
                channel.close();
            }
        }
        nettyServerGroup.shutdownGracefully();
    }

    public static boolean isSelf(String identity) {
        return StringUtils.equals(identity, localId());

    }

    public static void doHeartBeat() {
        for (Map.Entry<String, Channel> ety : nodes.entrySet()) {
            //发送心跳包给follower
            ety.getValue().writeAndFlush(
                    new HeartBeatPacket()
                            .setCurrTerm(StateManager.getTerm().get()).setLeaderId(localId())
                            .setLeaderTermUUID(localId() + "-" + StateManager.getTerm())
            );
        }
    }

    public static void setLeader(String leader) {
        ClusterManager.leader = leader;
    }

    public static String getLeader() {
        return leader;
    }

    public static void broadCast(Object packet) {
        log.debug("broadCast msg:{}", packet);
        for (Map.Entry<String, Channel> ety : nodes.entrySet()) {
            ety.getValue().writeAndFlush(packet);
        }
    }

    public static void remove(String shutDownIp) {
        if (getLeader().equals(shutDownIp)) {
            setLeader("leaderOffline");
        }
        nodes.remove(shutDownIp);
    }

    public static Channel getLeaderChannel() {
        return nodes.get(getLeader());
    }

    static Map<String, Integer> nodeIdMapHttpPort = new ConcurrentHashMap<>();

    public static Map<String, Integer> getNodeIdMapHttpPort() {
        HashMap<String, Integer> res = new HashMap<>(nodeIdMapHttpPort);
        res.put(ClusterManager.localId(), WRaftConfig.getInstance().getHttpPort());
        return res;
    }

    static Set<String> syncRecord = new HashSet<>();
    public static boolean firstHeartbeatSyncData(String leaderTermUUID) {
        return !syncRecord.contains(leaderTermUUID);
    }

    public static void recordFirstHeartBeatSyncData(String leaderTermUUID) {
        syncRecord.add(leaderTermUUID);
    }
}
