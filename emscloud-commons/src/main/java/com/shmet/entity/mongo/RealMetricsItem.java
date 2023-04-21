package com.shmet.entity.mongo;

import java.io.Serializable;
import java.util.Map;

public class RealMetricsItem implements Serializable {

    private static final long serialVersionUID = -1811866496414609237L;
    private Long timestamp;
    private Map<String, Object> datas;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, Object> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "RealMetricsItem{" +
            "timestamp=" + timestamp +
            '}';
    }
}
