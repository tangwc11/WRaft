package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

import java.util.Map;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class SyncAllDataRespPacket extends BasePacket{

    private String reqPeerId;
    private Map<String,String> data;
    private String lastCmtLogId;

    public String getReqPeerId() {
        return reqPeerId;
    }

    public SyncAllDataRespPacket setReqPeerId(String reqPeerId) {
        this.reqPeerId = reqPeerId;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public SyncAllDataRespPacket setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    public String getLastCmtLogId() {
        return lastCmtLogId;
    }

    public SyncAllDataRespPacket setLastCmtLogId(String lastCmtLogId) {
        this.lastCmtLogId = lastCmtLogId;
        return this;
    }

    @Override
    public int type() {
        return SYNC_ALL_DATA_RESP;
    }
}
