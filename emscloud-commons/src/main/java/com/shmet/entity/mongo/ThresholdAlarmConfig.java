package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "thresholdAlarmConfig")
@CompoundIndexes({
        @CompoundIndex(name = "thresholdalarm_deviceno_tagcode_idx", def = "{'deviceNo': 1, 'tagCode': 1}")
})
public class ThresholdAlarmConfig {
    @Id
    private String id;

    @NotNull
    private Long deviceNo;

    @NotNull
    private String tagCode;

    @NotNull
    private Double upperLimit = Double.MAX_VALUE;

    @NotNull
    private Double lowerLimit = Double.MIN_VALUE;

    @NotNull
    private String processBeanName;

    static public String getCollectionName() {
        return "thresholdAlarmConfig";
    }


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


    public Double getUpperLimit() {
        return upperLimit;
    }


    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }


    public Double getLowerLimit() {
        return lowerLimit;
    }


    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }


    public String getProcessBeanName() {
        return processBeanName;
    }


    public void setProcessBeanName(String processBeanName) {
        this.processBeanName = processBeanName;
    }

}
