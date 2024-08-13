package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class AppendLogAckPacket extends BasePacket{


    private String ackPeerId;
    private String cmdLogId;
    private String reqId;

    public String getAckPeerId() {
        return ackPeerId;
    }

    public AppendLogAckPacket setAckPeerId(String ackPeerId) {
        this.ackPeerId = ackPeerId;
        return this;
    }

    public String getCmdLogId() {
        return cmdLogId;
    }

    public AppendLogAckPacket setCmdLogId(String cmdLogId) {
        this.cmdLogId = cmdLogId;
        return this;
    }

    @Override
    public int type() {
        return APPEND_LOG_ACK;
    }

    public String getReqId() {
        return reqId;
    }

    public AppendLogAckPacket setReqId(String reqId) {
        this.reqId = reqId;
        return this;
    }
}
