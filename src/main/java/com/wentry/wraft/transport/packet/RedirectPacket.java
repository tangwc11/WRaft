package com.wentry.wraft.transport.packet;

import com.wentry.wraft.spring.annotation.WRaftPacket;
import lombok.ToString;

/**
 * @Description:
 * @Author: tangwc
 */
@WRaftPacket
@ToString
public class RedirectPacket extends BasePacket{


    private String key;
    private String val;
    private String refer;

    public String getKey() {
        return key;
    }

    public RedirectPacket setKey(String key) {
        this.key = key;
        return this;
    }

    public String getVal() {
        return val;
    }

    public RedirectPacket setVal(String val) {
        this.val = val;
        return this;
    }

    public String getRefer() {
        return refer;
    }

    public RedirectPacket setRefer(String refer) {
        this.refer = refer;
        return this;
    }

    @Override
    public int type() {
        return REDIRECT;
    }
}
