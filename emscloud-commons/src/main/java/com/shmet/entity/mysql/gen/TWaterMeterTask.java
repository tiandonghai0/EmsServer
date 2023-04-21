package com.shmet.entity.mysql.gen;

import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

@Table(name = "t_water_meter_task")
public class TWaterMeterTask {

    private Integer fId;
    private Integer fIsReset;
    private Integer fWaterMeterNumber;
    private String fCreateUser;
    private String fProcessStatus;
    private Integer fRechargeAmount;
    private String fStatus;
    private String fUpdateUser;
    private Date fCreateTime;
    private Date fTpdateTime;
    private Date fValidTime;

    public TWaterMeterTask() {
    }


    public Integer getfId() {
        return fId;
    }

    public void setfId(Integer fId) {
        this.fId = fId;
    }

    public Integer getfIsReset() {
        return fIsReset;
    }

    public void setfIsReset(Integer fIsReset) {
        this.fIsReset = fIsReset;
    }

    public Integer getfWaterMeterNumber() {
        return fWaterMeterNumber;
    }

    public void setfWaterMeterNumber(Integer fWaterMeterNumber) {
        this.fWaterMeterNumber = fWaterMeterNumber;
    }

    public String getfCreateUser() {
        return fCreateUser;
    }

    public void setfCreateUser(String fCreateUser) {
        this.fCreateUser = fCreateUser;
    }

    public String getfProcessStatus() {
        return fProcessStatus;
    }

    public void setfProcessStatus(String fProcessStatus) {
        this.fProcessStatus = fProcessStatus;
    }

    public Integer getfRechargeAmount() {
        return fRechargeAmount;
    }

    public void setfRechargeAmount(Integer fRechargeAmount) {
        this.fRechargeAmount = fRechargeAmount;
    }

    public String getfStatus() {
        return fStatus;
    }

    public void setfStatus(String fStatus) {
        this.fStatus = fStatus;
    }


    public String getfUpdateUser() {
        return fUpdateUser;
    }

    public void setfUpdateUser(String fUpdateUser) {
        this.fUpdateUser = fUpdateUser;
    }

    public Date getfCreateTime() {
        return fCreateTime;
    }

    public void setfCreateTime(Date fCreateTime) {
        this.fCreateTime = fCreateTime;
    }

    public Date getfTpdateTime() {
        return fTpdateTime;
    }

    public void setfTpdateTime(Date fTpdateTime) {
        this.fTpdateTime = fTpdateTime;
    }

    public Date getfValidTime() {
        return fValidTime;
    }

    public void setfValidTime(Date fValidTime) {
        this.fValidTime = fValidTime;
    }

}
