package com.shmet.bean;

public class SendTextCmdBean {
    Integer sendNo;
    Long acconeId;
    String textCmd;
    Integer command;
    // 1：communication; 2：web
    Integer senderId;
    String eventId;
    Integer status;
    String content;
    String deBug;
    Integer cmd;

    public Integer getSendNo() {
        return sendNo;
    }

    public void setSendNo(Integer sendNo) {
        this.sendNo = sendNo;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeBug() {
        return deBug;
    }

    public void setDeBug(String deBug) {
        this.deBug = deBug;
    }

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public String getTextCmd() {
        return textCmd;
    }

    public void setTextCmd(String textCmd) {
        this.textCmd = textCmd;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }
}
