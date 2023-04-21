package com.shmet.bean.waterMeter;

/**
 * 水表父类
 */
public class WaterMeterBaseInfo {
    private int code;
    private String msg;
    private Object data;
    private String meter_id;
    private String token;
    private String tra_id;
    private double amount;
    private String level;
    private String fWaterMeterNumber;

    public String getfWaterMeterNumber() {
        return fWaterMeterNumber;
    }

    public void setfWaterMeterNumber(String fWaterMeterNumber) {
        this.fWaterMeterNumber = fWaterMeterNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level1) {
        this.level = level1;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getTra_id() {
        return tra_id;
    }

    public void setTra_id(String tra_id) {
        this.tra_id = tra_id;
    }


    public String getMeter_id() {
        return meter_id;
    }

    public void setMeter_id(String meter_id) {
        this.meter_id = meter_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
