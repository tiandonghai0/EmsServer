package com.shmet.bean.waterMeter;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class WaterMeterTaskInfo {

    private Integer fRechargeAmount;
    private List<Integer> fWaterMeterNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fValidTime;

    private Integer fIsReset;

    public Integer getfRechargeAmount() {
        return fRechargeAmount;
    }

    public void setfRechargeAmount(Integer fRechargeAmount) {
        this.fRechargeAmount = fRechargeAmount;
    }

    public List<Integer> getfWaterMeterNumber() {
        return fWaterMeterNumber;
    }

    public void setfWaterMeterNumber(List<Integer> fWaterMeterNumber) {
        this.fWaterMeterNumber = fWaterMeterNumber;
    }

    public Date getfValidTime() {
        return fValidTime;
    }

    public void setfValidTime(Date fValidTime) {
        this.fValidTime = fValidTime;
    }

    public Integer getfIsReset() {
        return fIsReset;
    }

    public void setfIsReset(Integer fIsReset) {
        this.fIsReset = fIsReset;
    }
}
