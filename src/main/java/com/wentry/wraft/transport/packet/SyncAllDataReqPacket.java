package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class SyncAllDataReqPacket extends BasePacket{

    private String reqPeerId;

    public String getReqPeerId() {
        return reqPeerId;
    }

    public SyncAllDataReqPacket setReqPeerId(String reqPeerId) {
        this.reqPeerId = reqPeerId;
        return this;
    }

    @Override
    public int type() {
        return SYNC_ALL_DATA;
    }
}
