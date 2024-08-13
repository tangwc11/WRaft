package com.wentry.wraft.transport.packet;

/**
 * packet
 */
public interface Packet {

    //普通心跳
    int HEART_BEAT = 1;
    //心跳响应
    int HEART_BEAT_RESP = 2;
    //追加日志
    int APPEND_LOG = 3;
    //追加待提交
    int APPEND_LOG_ACK = 4;
    //追加确认提交
    int APPEND_LOG_CMT = 5;
    //请求发起选举
    int VOTE_REQ = 6;
    //选举投票
    int VOTE_RESP = 7;
    //请求转发
    int REDIRECT = 8;
    //集群构建
    int CLUSTER_CONNECT = 11;
    //开启服务
    int START = 12;
    //leader变更广播
    int LEADER_CHANGE = 13;
    //节点下线
    int SHUT_DOWN = 14;
    //全量数据同步请求
    int SYNC_ALL_DATA = 15;
    //全量数据同步返回
    int SYNC_ALL_DATA_RESP = 16;

    int type();

}
