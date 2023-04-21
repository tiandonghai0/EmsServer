package com.shmet.bean.Ehang365;


import java.util.List;

public class EHang365UsageUploadData{
    String dataStream;
    List<EHang365UsageUploadDataPoints> dataPoints;

    public String getDataStream() {
        return dataStream;
    }

    public void setDataStream(String dataStream) {
        this.dataStream = dataStream;
    }

    public List<EHang365UsageUploadDataPoints> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<EHang365UsageUploadDataPoints> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
