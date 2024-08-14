package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import com.wentry.wraft.storage.CmdLog;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class AppendLogReqPacket extends BasePacket{

    private CmdLog cmdLog;
    private String from;
    private String lastCmtLogId;
    private String reqId;

    public CmdLog getCmdLog() {
        return cmdLog;
    }

    public AppendLogReqPacket setCmdLog(CmdLog cmdLog) {
        this.cmdLog = cmdLog;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public AppendLogReqPacket setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getLastCmtLogId() {
        return lastCmtLogId;
    }

    public AppendLogReqPacket setLastCmtLogId(String lastCmtLogId) {
        this.lastCmtLogId = lastCmtLogId;
        return this;
    }

    @Override
    public int type() {
        return APPEND_LOG_REQ;
    }

    public String getReqId() {
        return reqId;
    }

    public AppendLogReqPacket setReqId(String reqId) {
        this.reqId = reqId;
        return this;
    }
}
