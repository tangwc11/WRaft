package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 *
 */
@WRaftPacket
@ToString
public class HeartBeatRespPacket extends BasePacket{

    private String respNodeId;

    public String getRespNodeId() {
        return respNodeId;
    }

    public HeartBeatRespPacket setRespNodeId(String respNodeId) {
        this.respNodeId = respNodeId;
        return this;
    }

    @Override
    public int type() {
        return HEART_BEAT_RESP;
    }
}
