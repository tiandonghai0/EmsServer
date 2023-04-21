package com.shmet.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RealDataItem implements Serializable {

  public RealDataItem() {
  }

  private static final long serialVersionUID = -1930780182179399158L;
  //设备编号
  String deviceNo;
  //点位
  String tagCode;
  String dateTime;
  Object data;
  //告警/故障详情信息(从redis缓存中获取)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String unit;
  //告警/故障  级别
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String content;

  public String getDeviceNo() {
    return deviceNo;
  }

  public void setDeviceNo(String deviceNo) {
    this.deviceNo = deviceNo;
  }

  public String getTagCode() {
    return tagCode;
  }

  public void setTagCode(String tagCode) {
    this.tagCode = tagCode;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUnit() {
    return unit;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "RealDataItem{" +
        "deviceNo='" + deviceNo + '\'' +
        ", tagCode='" + tagCode + '\'' +
        ", dateTime='" + dateTime + '\'' +
        ", data=" + data +
        ", unit='" + unit + '\'' +
        ", content='" + content + '\'' +
        '}';
  }
}
