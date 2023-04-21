package com.shmet.bean;

public class LxDataListBean {

    private String monsNo;
    private String collItemCode;
    private Object dataValue;
    private String collTime;
    private String dataTime;

    public void setMonsNo(String monsNo) {
        this.monsNo = monsNo;
    }

    public String getMonsNo() {
        return monsNo;
    }

    public void setCollItemCode(String collItemCode) {
        this.collItemCode = collItemCode;
    }

    public String getCollItemCode() {
        return collItemCode;
    }

    public void setDataValue(Object dataValue) {
        this.dataValue = dataValue;
    }

    public Object getDataValue() {
        return dataValue;
    }

    public void setCollTime(String collTime) {
        this.collTime = collTime;
    }

    public String getCollTime() {
        return collTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getDataTime() {
        return dataTime;
    }

}