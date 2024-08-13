package com.wentry.wraft.core;

/**
 * 节点状态，用于控制行为
 */
public enum NodeStats {

    /**
     * 初始状态
     */
    INITIAL,

    /**
     * 跟随着
     */
    FOLLOWER,

    /**
     * 参选者
     */
    CANDIDATE,

    /**
     * 领导者
     */
    LEADER

}
