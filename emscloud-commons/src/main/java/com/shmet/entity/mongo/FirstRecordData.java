package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 电量的初始读数，该读数作为统计的初始值被减去。
 *
 */
@Document(collection = "firstRecordData")
public class FirstRecordData {
    @Id
    private String id;

    // 设备号,唯一编号
    @NotNull
    private Long deviceNo;

    // 点位值
    @NotNull
    private String tagCode;

    // 初始读数
    @NotNull
    private double firstMeter;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Long getDeviceNo() {
        return deviceNo;
    }


    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getTagCode() {
        return tagCode;
    }


    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }


    public double getFirstMeter() {
        return firstMeter;
    }


    public void setFirstMeter(double firstMeter) {
        this.firstMeter = firstMeter;
    }

}
