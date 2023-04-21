package com.shmet.vo;

/**
 * @author
 */
public class AlarmVo {
  private String unit;
  private int id;
  private int flag;
  private String deviceNo;
  private String time;
  private String content;

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  public void setDeviceNo(String deviceNo) {
    this.deviceNo = deviceNo;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUnit() {
    return unit;
  }

  public int getId() {
    return id;
  }

  public int getFlag() {
    return flag;
  }

  public String getDeviceNo() {
    return deviceNo;
  }

  public String getTime() {
    return time;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "AlramVo{" +
        "unit='" + unit + '\'' +
        ", id=" + id +
        ", flag=" + flag +
        ", deviceNo='" + deviceNo + '\'' +
        ", time='" + time + '\'' +
        ", content='" + content + '\'' +
        '}';
  }

}
