package com.shmet.entity.mongo;

import java.util.List;


public class GroupMetricsItem {
  // 按分钟计，时间精确到分钟201901031526
  private Long timestamp;
  
  // sourceData例: [{deviceNo:XXXX, tagCode:点位名, last:最后读数, lastTimestamp:最后读数记录时间}, {}]
  private List<GroupSourceData> sourceData;
  
  private Double groupData;

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public List<GroupSourceData> getSourceData() {
    return sourceData;
  }

  public void setSourceData(List<GroupSourceData> sourceData) {
    this.sourceData = sourceData;
  }


  public Double getGroupData() {
    return groupData;
  }


  public void setGroupData(Double groupData) {
    this.groupData = groupData;
  }

}
