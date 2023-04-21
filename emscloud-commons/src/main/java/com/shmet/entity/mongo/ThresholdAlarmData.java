package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "thresholdAlarmData")
@CompoundIndexes({
        @CompoundIndex(name = "thresholdalarm_time_deviceno_tagcode_idx", def = "{'lastTime': -1, 'deviceNo': 1, 'tagCode': 1}")
})
public class ThresholdAlarmData {
    @Id
    private String id;

    @NotNull
    private Long deviceNo;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private String tagCode;

    @NotNull
    private Long firstTime;

    @NotNull
    private Long lastTime;

    @NotNull
    private Long acconeId;

    @NotNull
    private String acconeSn;

    // 计时单位为秒
    @NotNull
    private Long duration;

    @NotNull
    private List<RealMetricsItem> metrics;

    static public String getCollectionName() {
        return "thresholdAlarmData";
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


    public Integer getProjectId() {
        return projectId;
    }


    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }


    public Long getSubProjectId() {
        return subProjectId;
    }


    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }


    public String getTagCode() {
        return tagCode;
    }


    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }


    public Long getFirstTime() {
        return firstTime;
    }


    public void setFirstTime(Long firstTime) {
        this.firstTime = firstTime;
    }


    public Long getLastTime() {
        return lastTime;
    }


    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }


    public Long getDuration() {
        return duration;
    }


    public void setDuration(Long duration) {
        this.duration = duration;
    }


    public List<RealMetricsItem> getMetrics() {
        return metrics;
    }


    public void setMetrics(List<RealMetricsItem> metrics) {
        this.metrics = metrics;
    }


    public String getAcconeSn() {
        return acconeSn;
    }


    public void setAcconeSn(String acconeSn) {
        this.acconeSn = acconeSn;
    }

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    @Override
    public String toString() {
        return "ThresholdAlarmData [id=" + id + ", deviceNo=" + deviceNo + ", projectId=" + projectId
                + ", subProjectId=" + subProjectId + ", tagCode=" + tagCode + ", firstTime=" + firstTime
                + ", lastTime=" + lastTime + ", acconeId=" + acconeId + ", acconeSn=" + acconeSn
                + ", duration=" + duration + ", metrics=" + metrics + "]";
    }

}
