package com.shmet.bean.foreign;

import java.util.Date;

public class DeviceStatusInfo {
  private String ProjectID;
  private String PlatformID;
  private String DeviceID;
  private int DeviceStatus;
  private Date ChangeTime;
  private int InletElectricity;
  private int ExportElectricity;
  private Date FaultTime;
  private String FaultConditions;
  private Date FaultRepairTime;

  public void setProjectID(String ProjectID) {
    this.ProjectID = ProjectID;
  }

  public String getProjectID() {
    return ProjectID;
  }

  public void setPlatformID(String PlatformID) {
    this.PlatformID = PlatformID;
  }

  public String getPlatformID() {
    return PlatformID;
  }

  public void setDeviceID(String DeviceID) {
    this.DeviceID = DeviceID;
  }

  public String getDeviceID() {
    return DeviceID;
  }

  public void setDeviceStatus(int DeviceStatus) {
    this.DeviceStatus = DeviceStatus;
  }

  public int getDeviceStatus() {
    return DeviceStatus;
  }

  public void setChangeTime(Date ChangeTime) {
    this.ChangeTime = ChangeTime;
  }

  public Date getChangeTime() {
    return ChangeTime;
  }

  public void setInletElectricity(int InletElectricity) {
    this.InletElectricity = InletElectricity;
  }

  public int getInletElectricity() {
    return InletElectricity;
  }

  public void setExportElectricity(int ExportElectricity) {
    this.ExportElectricity = ExportElectricity;
  }

  public int getExportElectricity() {
    return ExportElectricity;
  }

  public void setFaultTime(Date FaultTime) {
    this.FaultTime = FaultTime;
  }

  public Date getFaultTime() {
    return FaultTime;
  }

  public void setFaultConditions(String FaultConditions) {
    this.FaultConditions = FaultConditions;
  }

  public String getFaultConditions() {
    return FaultConditions;
  }

  public void setFaultRepairTime(Date FaultRepairTime) {
    this.FaultRepairTime = FaultRepairTime;
  }

  public Date getFaultRepairTime() {
    return FaultRepairTime;
  }

}