package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * 心跳包
 */
@WRaftPacket
@ToString
public class HeartBeatPacket extends BasePacket{

    private int currTerm;
    private String leaderId;

    public int getCurrTerm() {
        return currTerm;
    }

    public HeartBeatPacket setCurrTerm(int currTerm) {
        this.currTerm = currTerm;
        return this;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public HeartBeatPacket setLeaderId(String leaderId) {
        this.leaderId = leaderId;
        return this;
    }

    @Override
    public int type() {
        return HEART_BEAT;
    }

}
