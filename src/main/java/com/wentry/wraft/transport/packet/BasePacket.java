package com.wentry.wraft.transport.packet;

/**
 * 事件模型
 */
public abstract class BasePacket implements Packet {

    private String base;

    public String getBase() {
        return base;
    }

    public BasePacket setBase(String base) {
        this.base = base;
        return this;
    }
}
