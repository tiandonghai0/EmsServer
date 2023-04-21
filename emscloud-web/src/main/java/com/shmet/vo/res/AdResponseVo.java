package com.shmet.vo.res;

import java.math.BigDecimal;

/**
 * @author
 */
public class AdResponseVo {
  private Integer id;
  //船舶编号
  private String berthId;
  //船只名称
  private String ShipName;
  //船舶状态
  private String berthStatus;
  //港口名称
  private String harborName;
  //设备编码
  private String DeviceID;
  //设备名称
  private String deviceName;
  //靠泊时间
  private String BerthTime;
  //开始用电时间
  private String StartTime;
  //开始电量
  private BigDecimal StartInletElectricity;
  //结束用电时间
  private String EndTime;
  //结束电量
  private BigDecimal EndInletElectricity;
  //用电量
  private BigDecimal UseElectricity;
  //离泊时间
  private String DepartureTime;
  //受电电压
  private BigDecimal RecvVoltage;
  //受电频率
  private BigDecimal RecvFrequency;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

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

  public String getBerthStatus() {
    return berthStatus;
  }

  public void setBerthStatus(String berthStatus) {
    this.berthStatus = berthStatus;
  }

  public String getHarborName() {
    return harborName;
  }

  public void setHarborName(String harborName) {
    this.harborName = harborName;
  }

  public String getDeviceID() {
    return DeviceID;
  }

  public void setDeviceID(String deviceID) {
    DeviceID = deviceID;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getBerthTime() {
    return BerthTime;
  }

  public void setBerthTime(String berthTime) {
    BerthTime = berthTime;
  }

  public String getStartTime() {
    return StartTime;
  }

  public void setStartTime(String startTime) {
    StartTime = startTime;
  }

  public BigDecimal getStartInletElectricity() {
    return StartInletElectricity;
  }

  public void setStartInletElectricity(BigDecimal startInletElectricity) {
    StartInletElectricity = startInletElectricity;
  }

  public String getEndTime() {
    return EndTime;
  }

  public void setEndTime(String endTime) {
    EndTime = endTime;
  }

  public BigDecimal getEndInletElectricity() {
    return EndInletElectricity;
  }

  public void setEndInletElectricity(BigDecimal endInletElectricity) {
    EndInletElectricity = endInletElectricity;
  }

  public BigDecimal getUseElectricity() {
    return UseElectricity;
  }

  public void setUseElectricity(BigDecimal useElectricity) {
    UseElectricity = useElectricity;
  }

  public String getDepartureTime() {
    return DepartureTime;
  }

  public void setDepartureTime(String departureTime) {
    DepartureTime = departureTime;
  }

  public BigDecimal getRecvVoltage() {
    return RecvVoltage;
  }

  public void setRecvVoltage(BigDecimal recvVoltage) {
    RecvVoltage = recvVoltage;
  }

  public BigDecimal getRecvFrequency() {
    return RecvFrequency;
  }

  public void setRecvFrequency(BigDecimal recvFrequency) {
    RecvFrequency = recvFrequency;
  }
}
