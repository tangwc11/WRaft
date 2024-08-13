package com.wentry.wraft.pojo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投票结果
 */
public class VoteResult {


    private AtomicInteger voted = new AtomicInteger();
    private int total;
    private boolean finished;

    public AtomicInteger getVoted() {
        return voted;
    }

    public VoteResult setVoted(AtomicInteger voted) {
        this.voted = voted;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public VoteResult setTotal(int total) {
        this.total = total;
        return this;
    }

    public boolean isFinished() {
        return finished;
    }

    public VoteResult setFinished(boolean finished) {
        this.finished = finished;
        return this;
    }

    @Override
    public String toString() {
        return "VoteResult{" +
                "voted=" + voted +
                ", total=" + total +
                ", finished=" + finished +
                '}';
    }
}
