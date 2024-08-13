package com.wentry.wraft.core;

import com.wentry.wraft.storage.StorageManager;
import com.wentry.wraft.transport.packet.RedirectPacket;
import io.netty.channel.Channel;

/**
 * 日志维护
 */
public class AppendLogManager {

    /**
     * 涉及请求转发
     */
    public static String set(String key, String val) {

        if (StateManager.isLeader()) {
            StorageManager.appendLog(key, val);
            return "direct op to leader";
        }

        Channel leader = ClusterManager.getLeaderChannel();
        if (leader == null) {
            return "no leader, please try again";
        }
        leader.writeAndFlush(
                new RedirectPacket().setKey(key).setVal(val).setRefer(ClusterManager.localId())
        );
        return "redirect success";
    }
}
