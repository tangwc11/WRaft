package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * 投票响应
 */
@WRaftPacket
@ToString
public class VoteRespPacket extends BasePacket{

    private int term;
    private String voterId;

    public int getTerm() {
        return term;
    }

    public VoteRespPacket setTerm(int term) {
        this.term = term;
        return this;
    }

    public String getVoterId() {
        return voterId;
    }

    public VoteRespPacket setVoterId(String voterId) {
        this.voterId = voterId;
        return this;
    }

    @Override
    public int type() {
        return VOTE_RESP;
    }

}
