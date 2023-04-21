package com.shmet.entity.mysql.gen;

import java.math.*;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//上港岸电实体
@TableName(value = "t_ad_connrecordinfo")
public class AdConnrecordinfo {
  @TableId(type = IdType.AUTO)
  private Integer id;
  //连船记录编号
  @TableField(value = "ConnRecordSeq")
  private String ConnRecordSeq;
  @TableField(value = "ProjectID")
  private String ProjectID;
  @TableField(value = "PlatformID")
  private String PlatformID;
  //设备编码
  @TableField(value = "DeviceID")
  private String DeviceID;
  @TableField(value = "date")
  private String date;
  //船只名称
  @TableField(value = "ShipName")
  private String ShipName;
  //靠泊时间
  @TableField(value = "BerthTime")
  private String BerthTime;
  //离泊时间
  @TableField(value = "DepartureTime")
  private String DepartureTime;
  //开始用电时间
  @TableField(value = "StartTime")
  private String StartTime;
  @TableField(value = "EndTime")
  private String EndTime;
  @TableField(value = "StartInletElectricity")
  private BigDecimal StartInletElectricity;
  @TableField(value = "StartExportElectricity")
  private BigDecimal StartExportElectricity;
  @TableField(value = "EndInletElectricity")
  private BigDecimal EndInletElectricity;
  @TableField(value = "EndExportElectricity")
  private BigDecimal EndExportElectricity;
  @TableField(value = "MaxCurrent")
  private BigDecimal MaxCurrent;
  @TableField(value = "MaxPower")
  private BigDecimal MaxPower;
  @TableField(value = "SupplyElectricity")
  private BigDecimal SupplyElectricity;
  @TableField(value = "UseElectricity")
  //用电量
  private BigDecimal UseElectricity;
  @TableField(value = "SupplyDuration")
  private BigDecimal SupplyDuration;
  @TableField(value = "AuxiliaryPower")
  //辅机额定功率 单位 kW
  private BigDecimal AuxiliaryPower;
  @TableField(value = "RecvVoltage")
  //受电电压 单位kV
  private BigDecimal RecvVoltage;
  @TableField(value = "RecvFrequency")
  //受电频率 单位Hz
  private BigDecimal RecvFrequency;
  @TableField(value = "IsPush")
  private Integer IsPush;
  @TableField(value = "berthStatus")
  //船舶状态
  private String berthStatus;
  //船舶编号
  @TableField(value = "berthId")
  private String berthId;
  //港口名称
  @TableField(value = "harborName")
  private String harborName;
  //设备名称
  @TableField(value = "deviceName")
  private String deviceName;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getConnRecordSeq() {
    return ConnRecordSeq;
  }

  public void setConnRecordSeq(String connRecordSeq) {
    ConnRecordSeq = connRecordSeq;
  }

  public String getProjectID() {
    return ProjectID;
  }

  public void setProjectID(String projectID) {
    ProjectID = projectID;
  }

  public String getPlatformID() {
    return PlatformID;
  }

  public void setPlatformID(String platformID) {
    PlatformID = platformID;
  }

  public String getDeviceID() {
    return DeviceID;
  }

  public void setDeviceID(String deviceID) {
    DeviceID = deviceID;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getShipName() {
    return ShipName;
  }

  public void setShipName(String shipName) {
    ShipName = shipName;
  }

  public String getBerthTime() {
    return BerthTime;
  }

  public void setBerthTime(String berthTime) {
    BerthTime = berthTime;
  }

  public String getDepartureTime() {
    return DepartureTime;
  }

  public void setDepartureTime(String departureTime) {
    DepartureTime = departureTime;
  }

  public String getStartTime() {
    return StartTime;
  }

  public void setStartTime(String startTime) {
    StartTime = startTime;
  }

  public String getEndTime() {
    return EndTime;
  }

  public void setEndTime(String endTime) {
    EndTime = endTime;
  }

  public BigDecimal getStartInletElectricity() {
    return StartInletElectricity;
  }

  public void setStartInletElectricity(BigDecimal startInletElectricity) {
    StartInletElectricity = startInletElectricity;
  }

  public BigDecimal getStartExportElectricity() {
    return StartExportElectricity;
  }

  public void setStartExportElectricity(BigDecimal startExportElectricity) {
    StartExportElectricity = startExportElectricity;
  }

  public BigDecimal getEndInletElectricity() {
    return EndInletElectricity;
  }

  public void setEndInletElectricity(BigDecimal endInletElectricity) {
    EndInletElectricity = endInletElectricity;
  }

  public BigDecimal getEndExportElectricity() {
    return EndExportElectricity;
  }

  public void setEndExportElectricity(BigDecimal endExportElectricity) {
    EndExportElectricity = endExportElectricity;
  }

  public BigDecimal getMaxCurrent() {
    return MaxCurrent;
  }

  public void setMaxCurrent(BigDecimal maxCurrent) {
    MaxCurrent = maxCurrent;
  }

  public BigDecimal getMaxPower() {
    return MaxPower;
  }

  public void setMaxPower(BigDecimal maxPower) {
    MaxPower = maxPower;
  }

  public BigDecimal getSupplyElectricity() {
    return SupplyElectricity;
  }

  public void setSupplyElectricity(BigDecimal supplyElectricity) {
    SupplyElectricity = supplyElectricity;
  }

  public BigDecimal getUseElectricity() {
    return UseElectricity;
  }

  public void setUseElectricity(BigDecimal useElectricity) {
    UseElectricity = useElectricity;
  }

  public BigDecimal getSupplyDuration() {
    return SupplyDuration;
  }

  public void setSupplyDuration(BigDecimal supplyDuration) {
    SupplyDuration = supplyDuration;
  }

  public BigDecimal getAuxiliaryPower() {
    return AuxiliaryPower;
  }

  public void setAuxiliaryPower(BigDecimal auxiliaryPower) {
    AuxiliaryPower = auxiliaryPower;
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

  public Integer getIsPush() {
    return IsPush;
  }

  public void setIsPush(Integer isPush) {
    IsPush = isPush;
  }

  public String getBerthStatus() {
    return berthStatus;
  }

  public void setBerthStatus(String berthStatus) {
    this.berthStatus = berthStatus;
  }

  public String getBerthId() {
    return berthId;
  }

  public void setBerthId(String berthId) {
    this.berthId = berthId;
  }

  public String getHarborName() {
    return harborName;
  }

  public void setHarborName(String harborName) {
    this.harborName = harborName;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }
}
