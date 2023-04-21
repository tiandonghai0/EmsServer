package com.shmet.entity.mysql.gen;

import java.util.Date;

import org.beetl.sql.core.annotatoin.Table;

@Table(name = "t_water_meter_record")
public class TWaterMeterRecord {

    private Integer fId;
    private String fCreateUser;
    private Integer fMaterMeterTaskId;
    private String fStatus;
    private String fTraId;
    private String fType;
    private String fUpdateUser;
    private String fWaterMeterNumber;
    private Date fCreateTime;
    private Date fUpdateTime;

    public TWaterMeterRecord() {
    }

    public Integer getfId() {
        return fId;
    }

    public void setfId(Integer fId) {
        this.fId = fId;
    }

    public String getfCreateUser() {
        return fCreateUser;
    }

    public void setfCreateUser(String fCreateUser) {
        this.fCreateUser = fCreateUser;
    }

    public Integer getfMaterMeterTaskId() {
        return fMaterMeterTaskId;
    }

    public void setfMaterMeterTaskId(Integer fMaterMeterTaskId) {
        this.fMaterMeterTaskId = fMaterMeterTaskId;
    }

    public String getfStatus() {
        return fStatus;
    }

    public void setfStatus(String fStatus) {
        this.fStatus = fStatus;
    }

    public String getfTraId() {
        return fTraId;
    }

    public void setfTraId(String fTraId) {
        this.fTraId = fTraId;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getfUpdateUser() {
        return fUpdateUser;
    }

    public void setfUpdateUser(String fUpdateUser) {
        this.fUpdateUser = fUpdateUser;
    }

    public String getfWaterMeterNumber() {
        return fWaterMeterNumber;
    }

    public void setfWaterMeterNumber(String fWaterMeterNumber) {
        this.fWaterMeterNumber = fWaterMeterNumber;
    }

    public Date getfCreateTime() {
        return fCreateTime;
    }

    public void setfCreateTime(Date fCreateTime) {
        this.fCreateTime = fCreateTime;
    }

    public Date getfUpdateTime() {
        return fUpdateTime;
    }

    public void setfUpdateTime(Date fUpdateTime) {
        this.fUpdateTime = fUpdateTime;
    }

}
