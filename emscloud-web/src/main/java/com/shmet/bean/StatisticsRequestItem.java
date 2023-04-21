package com.shmet.bean;

import java.util.List;

public class StatisticsRequestItem {
    String deviceNo;
    List<String> tagCodeList;

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public List<String> getTagCodeList() {
        return tagCodeList;
    }

    public void setTagCodeList(List<String> tagCodeList) {
        this.tagCodeList = tagCodeList;
    }

}
