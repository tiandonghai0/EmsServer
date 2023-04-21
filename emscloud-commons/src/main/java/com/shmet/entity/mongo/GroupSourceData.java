package com.shmet.entity.mongo;

public class GroupSourceData {
    private Long lastTimestamp;

    private Long deviceNo;

    private String tagCode;

    private Double last;


    public Long getLastTimestamp() {
        return lastTimestamp;
    }


    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }


    public Long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }


    public String getTagCode() {
        return tagCode;
    }


    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }


    public Double getLast() {
        return last;
    }


    public void setLast(Double last) {
        this.last = last;
    }


}
