package com.shmet.bean.waterMeter;

import java.util.Date;
import java.util.List;

public class GetAllRemainByLevel {

    private int code;
    private List<Data_> data;
    private String level1;
    private String msg;
    private String token;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setData(List<Data_> data) {
        this.data = data;
    }

    public List<Data_> getData() {
        return data;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel1() {
        return level1;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}

class Data_ {

    private Date read_time;
    private String data;
    private String meter_id;
    private int price;
    private String total_e;

    public void setRead_time(Date read_time) {
        this.read_time = read_time;
    }

    public Date getRead_time() {
        return read_time;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setMeter_id(String meter_id) {
        this.meter_id = meter_id;
    }

    public String getMeter_id() {
        return meter_id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setTotal_e(String total_e) {
        this.total_e = total_e;
    }

    public String getTotal_e() {
        return total_e;
    }

}

