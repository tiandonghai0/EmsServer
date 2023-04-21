package com.shmet.bean;

import java.io.Serializable;
import java.util.Map;

public class StatisticsDataItem implements Serializable {
    private static final long serialVersionUID = -1930780182179399158L;
    long deviceNo;
    String tagCode;
    int hour;
    Map<String, Object> dataMap;

    public long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(long deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

}
