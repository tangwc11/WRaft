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
public class AppendLogCmtPacket extends BasePacket{

    private String lastCmtLogId;
    private String cmtLogId;
    private CmdLog cmdLog;

    public String getLastCmtLogId() {
        return lastCmtLogId;
    }

    public AppendLogCmtPacket setLastCmtLogId(String lastCmtLogId) {
        this.lastCmtLogId = lastCmtLogId;
        return this;
    }

    public String getCmtLogId() {
        return cmtLogId;
    }

    public AppendLogCmtPacket setCmtLogId(String cmtLogId) {
        this.cmtLogId = cmtLogId;
        return this;
    }

    @Override
    public int type() {
        return APPEND_LOG_CMT;
    }

    public CmdLog getCmdLog() {
        return cmdLog;
    }

    public AppendLogCmtPacket setCmdLog(CmdLog cmdLog) {
        this.cmdLog = cmdLog;
        return this;
    }
}
