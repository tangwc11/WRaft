package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * 选举请求数据包
 */
@WRaftPacket
@ToString
public class VoteReqPacket extends BasePacket{

    private int term;
    private String candidateId;

    public int getTerm() {
        return term;
    }

    public VoteReqPacket setTerm(int term) {
        this.term = term;
        return this;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public VoteReqPacket setCandidateId(String candidateId) {
        this.candidateId = candidateId;
        return this;
    }

    @Override
    public int type() {
        return VOTE_REQ;
    }
}
