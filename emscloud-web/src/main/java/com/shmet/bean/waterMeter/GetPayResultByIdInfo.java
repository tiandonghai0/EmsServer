package com.shmet.bean.waterMeter;

import java.util.Date;

/**
 * 说明：参数 code=0 查询失败或认证失败，
 * code=1 充值成功，code=2 充值失败（24 小时内会继续发送失败的充值数据），
 * code=3 未执行， code=4 正在执行
 * code=5 数据不存在，
 * 参数 sent_time=充值成功或失败的时间
 * 参数 tra_id=交易号
 */
public class GetPayResultByIdInfo {

    private int code;
    private Data data;
    private String meter_id;
    private String msg;
    private Date sent_time;
    private String token;
    private String tra_id;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setMeter_id(String meter_id) {
        this.meter_id = meter_id;
    }

    public String getMeter_id() {
        return meter_id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setSent_time(Date sent_time) {
        this.sent_time = sent_time;
    }

    public Date getSent_time() {
        return sent_time;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setTra_id(String tra_id) {
        this.tra_id = tra_id;
    }

    public String getTra_id() {
        return tra_id;
    }

}


/**
 *
 */
class Sent_time {

    private int date;
    private int day;
    private int hours;
    private int minutes;
    private int month;
    private int nanos;
    private int seconds;
    private long time;
    private int timezoneOffset;
    private int year;

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getHours() {
        return hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    public int getNanos() {
        return nanos;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

}

class Data {

    private String this_time_pay_money;
    private Sent_time sent_time;
    private String tra_id;
    private int cong_value_beilv;
    private double curr_remain;
    private int money;
    private double price;
    private double after_pay_wallet;
    private int is_ok;
    private String before_pay_wallet;

    public void setThis_time_pay_money(String this_time_pay_money) {
        this.this_time_pay_money = this_time_pay_money;
    }

    public String getThis_time_pay_money() {
        return this_time_pay_money;
    }

    public void setSent_time(Sent_time sent_time) {
        this.sent_time = sent_time;
    }

    public Sent_time getSent_time() {
        return sent_time;
    }

    public void setTra_id(String tra_id) {
        this.tra_id = tra_id;
    }

    public String getTra_id() {
        return tra_id;
    }

    public void setCong_value_beilv(int cong_value_beilv) {
        this.cong_value_beilv = cong_value_beilv;
    }

    public int getCong_value_beilv() {
        return cong_value_beilv;
    }

    public void setCurr_remain(double curr_remain) {
        this.curr_remain = curr_remain;
    }

    public double getCurr_remain() {
        return curr_remain;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setAfter_pay_wallet(double after_pay_wallet) {
        this.after_pay_wallet = after_pay_wallet;
    }

    public double getAfter_pay_wallet() {
        return after_pay_wallet;
    }

    public void setIs_ok(int is_ok) {
        this.is_ok = is_ok;
    }

    public int getIs_ok() {
        return is_ok;
    }

    public void setBefore_pay_wallet(String before_pay_wallet) {
        this.before_pay_wallet = before_pay_wallet;
    }

    public String getBefore_pay_wallet() {
        return before_pay_wallet;
    }

}
