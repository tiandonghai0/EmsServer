package com.shmet.bean.waterMeter;

/**
 * 根据 token 和设备 id 查询余量/已使用量
 * http://ip:port/userInfo/getRemainById
 * 参数 meter_id：设备 id
 * 参数 code：1=查询成功，2= 数据不存在，0=认证失败
 * 参数 data：剩余量
 * 参数 total_e：总使用量
 * 参数 price：单价
 * 参数 read_time：数据采集到的时间
 * 参数 msg：查询结果描述
 */
public class GetRemainByIdInfo extends WaterMeterBaseInfo {

    private double price;
    private String total_e;
    private String read_time;


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTotal_e() {
        return total_e;
    }

    public void setTotal_e(String total_e) {
        this.total_e = total_e;
    }

    public String getRead_time() {
        return read_time;
    }

    public void setRead_time(String read_time) {
        this.read_time = read_time;
    }

}
