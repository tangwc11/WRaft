package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class ShutDownPacket extends BasePacket{

    private String shutDownIp;

    public String getShutDownIp() {
        return shutDownIp;
    }

    public ShutDownPacket setShutDownIp(String shutDownIp) {
        this.shutDownIp = shutDownIp;
        return this;
    }

    @Override
    public int type() {
        return SHUT_DOWN;
    }
}
