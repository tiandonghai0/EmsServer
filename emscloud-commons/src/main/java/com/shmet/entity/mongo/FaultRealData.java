package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "faultRealData")
@CompoundIndexes({
    @CompoundIndex(name = "fault_hour_idx", def = "{'deviceNo': 1, 'hour': -1}")
})
public class FaultRealData {
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
  private String acconeSn;

  @NotNull
  private Long acconeId;

  @NotNull
  private Integer hour;

  /**
   * 设备错误段的指标项，此处使用分桶设计
   */
  private List<RealMetricsItem> metrics;


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

  public Integer getHour() {
    return hour;
  }


  public void setHour(Integer hour) {
    this.hour = hour;
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

}
