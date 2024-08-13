package com.wentry.wraft.storage;

/**
 * 数据操作日志
 */
public class CmdLog {

    //数据的操作，只有三种，增删改
    public static final int ACTION_ADD = 1;
    public static final int ACTION_UPDATE = 2;
    public static final int ACTION_DEL = 3;
    private int action;

    private String logId;
    private String key;
    private String val;

    public String getLogId() {
        return logId;
    }

    public CmdLog setLogId(String logId) {
        this.logId = logId;
        return this;
    }

    public int getAction() {
        return action;
    }

    public CmdLog setAction(int action) {
        this.action = action;
        return this;
    }

    public String getKey() {
        return key;
    }

    public CmdLog setKey(String key) {
        this.key = key;
        return this;
    }

    public String getVal() {
        return val;
    }

    public CmdLog setVal(String val) {
        this.val = val;
        return this;
    }
}
