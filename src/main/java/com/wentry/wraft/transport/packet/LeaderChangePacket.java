package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class LeaderChangePacket extends BasePacket{

    private String preLeaderId;
    private String newLeaderId;
    private int newTerm;

    public String getPreLeaderId() {
        return preLeaderId;
    }

    public LeaderChangePacket setPreLeaderId(String preLeaderId) {
        this.preLeaderId = preLeaderId;
        return this;
    }

    public String getNewLeaderId() {
        return newLeaderId;
    }

    public LeaderChangePacket setNewLeaderId(String newLeaderId) {
        this.newLeaderId = newLeaderId;
        return this;
    }

    public int getNewTerm() {
        return newTerm;
    }

    public LeaderChangePacket setNewTerm(int newTerm) {
        this.newTerm = newTerm;
        return this;
    }

    @Override
    public int type() {
        return LEADER_CHANGE;
    }
}
