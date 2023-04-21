package com.shmet.bean.Ehang365;

import java.util.List;

public class EHang365UsageUpload {
    List<EHang365UsageUploadData> data;
    String deviceCode;
    public List<EHang365UsageUploadData> getData() {
        return data;
    }

    public void setData(List<EHang365UsageUploadData> data) {
        this.data = data;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

}
