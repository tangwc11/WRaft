package com.wentry.wraft.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 状态和角色管理
 */
public class StateManager {

    private static final AtomicReference<NodeStats> CURR_STATE = new AtomicReference<>(NodeStats.INITIAL);
    private static AtomicInteger term = new AtomicInteger(1);

    public static boolean isLeader() {
        return expect(NodeStats.LEADER);
    }

    public static boolean isInitial() {
        return expect(NodeStats.INITIAL);
    }

    public static boolean isFollower() {
        return expect(NodeStats.FOLLOWER);
    }

    public static boolean isCandidate() {
        return expect(NodeStats.CANDIDATE);
    }

    private static boolean expect(NodeStats initial) {
        return CURR_STATE.get() == initial;
    }

    public static boolean compareAndSet(List<NodeStats> old, NodeStats newState) {
        for (NodeStats oldState : old) {
            if (CURR_STATE.compareAndSet(oldState, newState)) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareAndSet(NodeStats old, NodeStats newState) {
        return CURR_STATE.compareAndSet(old, newState);
    }

    public static void changeState(NodeStats newState) {
        CURR_STATE.set(newState);
    }

    public static AtomicInteger getTerm() {
        return term;
    }

    public static void setTerm(int term) {
        StateManager.term = new AtomicInteger(term);
    }

    public static NodeStats currState() {
        return CURR_STATE.get();
    }
}
