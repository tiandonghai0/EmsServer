package com.shmet.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author
 */
public class AdCreateReq {
  //船舶编号
  @JsonProperty(value = "berthId")
  private String berthId;
  //船只名称
  @JsonProperty(value = "ShipName")
  private String ShipName;
  //停泊码头(港口名称)
  @JsonProperty(value = "ProjectID")
  private String ProjectID;
  //停靠泊位(设备编号 设备编码)
  @JsonProperty(value = "DeviceID")
  private String DeviceID;
  //靠泊时间
  @JsonProperty(value = "BerthTime")
  private String BerthTime;
  //设备名称 由前端 根据 设备编码(停靠泊位) 计算得出
  @JsonProperty(value = "deviceName")
  private String deviceName;
  //港口名称 由前端 根据 停泊码头(停泊港口 项目ID ProjectID) 计算得出
  @JsonProperty(value = "harborName")
  private String harborName;

  public String getBerthId() {
    return berthId;
  }

  public void setBerthId(String berthId) {
    this.berthId = berthId;
  }

  public String getShipName() {
    return ShipName;
  }

  public void setShipName(String shipName) {
    ShipName = shipName;
  }

  public String getProjectID() {
    return ProjectID;
  }

  public void setProjectID(String projectID) {
    ProjectID = projectID;
  }

  public String getDeviceID() {
    return DeviceID;
  }

  public void setDeviceID(String deviceID) {
    DeviceID = deviceID;
  }

  public String getBerthTime() {
    return BerthTime;
  }

  public void setBerthTime(String berthTime) {
    BerthTime = berthTime;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getHarborName() {
    return harborName;
  }

  public void setHarborName(String harborName) {
    this.harborName = harborName;
  }

  @Override
  public String toString() {
    return "AdCreateReq{" +
        "berthId='" + berthId + '\'' +
        ", ShipName='" + ShipName + '\'' +
        ", ProjectID='" + ProjectID + '\'' +
        ", DeviceID='" + DeviceID + '\'' +
        ", BerthTime='" + BerthTime + '\'' +
        ", deviceName='" + deviceName + '\'' +
        ", harborName='" + harborName + '\'' +
        '}';
  }
}
