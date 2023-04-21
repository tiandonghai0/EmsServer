package com.shmet.entity.mongo;

import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deviceDayStatistics")
@CompoundIndexes({@CompoundIndex(name = "device_day_idx", def = "{'deviceNo': 1, 'day': -1}")})
public class DeviceDayStatistics {
    @Id
    private String id;

    @NotNull
    private Long deviceNo;

    @NotNull
    private Integer day;

    @NotNull
    private Map<String, Map<String, Object>> statistics;

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


    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Map<String, Map<String, Object>> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Map<String, Object>> statistics) {
        this.statistics = statistics;
    }

}
