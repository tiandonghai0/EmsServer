package com.shmet.entity.mongo;


public class GroupDeviceTagItem {
  // 按分钟计，时间精确到分钟
  private Long deviceNo;
  private String tagCode;


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

}
