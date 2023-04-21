package com.shmet.bean;

import java.util.List;

import com.shmet.entity.mongo.DeviceRealData;

public class DeviceRealDataBean {
    Long acconeId;
    //设备实时数据
    List<DeviceRealData> deviceRealDataList;

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public List<DeviceRealData> getDeviceRealDataList() {
        return deviceRealDataList;
    }

    public void setDeviceRealDataList(List<DeviceRealData> deviceRealDataList) {
        this.deviceRealDataList = deviceRealDataList;
    }

}
