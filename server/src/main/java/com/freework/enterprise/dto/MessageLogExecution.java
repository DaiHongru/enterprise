package com.freework.enterprise.dto;


import com.freework.enterprise.entity.MessageLog;
import com.freework.enterprise.enums.MessageLogStateEnum;

import java.util.List;

/**
 * @author daihongru
 */
public class MessageLogExecution {
    /**
     * 结果状态
     */
    private int state;

    /**
     * 状态标识
     */
    private String stateInfo;

    /**
     * 消息数量
     */
    private int count;

    /**
     * MessageLog
     */
    private MessageLog messageLog;

    /**
     * MessageLog列表
     */
    private List<MessageLog> messageLogList;

    public MessageLogExecution() {
    }

    /**
     * 操作失败的时候使用的构造器，存储错误信息
     */
    public MessageLogExecution(MessageLogStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    /**
     * 操作成功的时候使用的构造器，存储一个MessageLog对象
     */

    public MessageLogExecution(MessageLogStateEnum stateEnum, MessageLog messageLog) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.messageLog = messageLog;
    }

    /**
     * 操作成功的时候使用的构造器，存储MessageLog对象列表
     */
    public MessageLogExecution(MessageLogStateEnum stateEnum, List<MessageLog> messageLogList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.messageLogList = messageLogList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public MessageLog getMessageLog() {
        return messageLog;
    }

    public void setMessageLog(MessageLog messageLog) {
        this.messageLog = messageLog;
    }

    public List<MessageLog> getMessageLogList() {
        return messageLogList;
    }

    public void setMessageLogList(List<MessageLog> messageLogList) {
        this.messageLogList = messageLogList;
    }
}
