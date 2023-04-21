package com.shmet.entity.mongo;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 使用分桶设计，以小时为单位存储设备的实时数据。
 * 如电流：
 * metrics:{current:{20180829221340:23, 20180829221540:25}}
 */
@Document(collection = "deviceRealData")
@CompoundIndexes({
    @CompoundIndex(name = "device_hour_idx", def = "{'deviceNo': 1, 'hour': -1}")
})
public class DeviceRealData {
  @Id
  private String id;

  @NotNull
  private Long deviceNo;

  @NotNull
  private Integer projectId;

  @NotNull
  private Long subProjectId;

  @NotNull
  private Integer deviceId;

  @NotNull
  private String deviceModel;

  @NotNull
  private Long acconeId;

  @NotNull
  private String acconeSn;

  @NotNull
  private int hour;

  /**
   * 设备的指标项，如电量，此处使用分桶设计
   * metrics:[timestamp:20180829221340, kwhTotal:[12,23], P:345]
   */
  private List<RealMetricsItem> metrics;

  /**
   * 按小时统计的字段
   */
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

  public Integer getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(Integer deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public List<RealMetricsItem> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<RealMetricsItem> metrics) {
    this.metrics = metrics;
  }

  public Map<String, Map<String, Object>> getStatistics() {
    return statistics;
  }

  public void setStatistics(Map<String, Map<String, Object>> statistics) {
    this.statistics = statistics;
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
    return "DeviceRealData{" +
        "id='" + id + '\'' +
        ", deviceNo=" + deviceNo +
        ", projectId=" + projectId +
        ", subProjectId=" + subProjectId +
        ", deviceId=" + deviceId +
        ", deviceModel='" + deviceModel + '\'' +
        ", acconeId=" + acconeId +
        ", acconeSn='" + acconeSn + '\'' +
        ", hour=" + hour +
        ", metrics=" + metrics +
        ", statistics=" + statistics +
        '}';
  }
}
