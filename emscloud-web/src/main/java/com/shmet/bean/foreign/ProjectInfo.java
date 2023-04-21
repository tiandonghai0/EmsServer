package com.shmet.bean.foreign;

import java.util.Date;
import java.util.List;

public class ProjectInfo {
  private String PlatformID;
  private String PortID;
  private String PortAreaID;
  private String OperatorName;
  private int Area;
  private String ProjectID;
  private String ProjectName;
  private int BerthNum;
  private int BerthType;
  private int ConstructionType;
  private int Tonnage;
  private int DeviceNum;
  private int FrequencyType;
  private int TotalInvestment;
  private String ManufacturerID;
  private String ManufacturerName;
  private Date BuiltTime;
  private List<DeviceInfos> DeviceInfos;

  public void setPlatformID(String PlatformID) {
    this.PlatformID = PlatformID;
  }

  public String getPlatformID() {
    return PlatformID;
  }

  public void setPortID(String PortID) {
    this.PortID = PortID;
  }

  public String getPortID() {
    return PortID;
  }

  public void setPortAreaID(String PortAreaID) {
    this.PortAreaID = PortAreaID;
  }

  public String getPortAreaID() {
    return PortAreaID;
  }

  public void setOperatorName(String OperatorName) {
    this.OperatorName = OperatorName;
  }

  public String getOperatorName() {
    return OperatorName;
  }

  public void setArea(int Area) {
    this.Area = Area;
  }

  public int getArea() {
    return Area;
  }

  public void setProjectID(String ProjectID) {
    this.ProjectID = ProjectID;
  }

  public String getProjectID() {
    return ProjectID;
  }

  public void setProjectName(String ProjectName) {
    this.ProjectName = ProjectName;
  }

  public String getProjectName() {
    return ProjectName;
  }

  public void setBerthNum(int BerthNum) {
    this.BerthNum = BerthNum;
  }

  public int getBerthNum() {
    return BerthNum;
  }

  public void setBerthType(int BerthType) {
    this.BerthType = BerthType;
  }

  public int getBerthType() {
    return BerthType;
  }

  public void setConstructionType(int ConstructionType) {
    this.ConstructionType = ConstructionType;
  }

  public int getConstructionType() {
    return ConstructionType;
  }

  public void setTonnage(int Tonnage) {
    this.Tonnage = Tonnage;
  }

  public int getTonnage() {
    return Tonnage;
  }

  public void setDeviceNum(int DeviceNum) {
    this.DeviceNum = DeviceNum;
  }

  public int getDeviceNum() {
    return DeviceNum;
  }

  public void setFrequencyType(int FrequencyType) {
    this.FrequencyType = FrequencyType;
  }

  public int getFrequencyType() {
    return FrequencyType;
  }

  public void setTotalInvestment(int TotalInvestment) {
    this.TotalInvestment = TotalInvestment;
  }

  public int getTotalInvestment() {
    return TotalInvestment;
  }

  public void setManufacturerID(String ManufacturerID) {
    this.ManufacturerID = ManufacturerID;
  }

  public String getManufacturerID() {
    return ManufacturerID;
  }

  public void setManufacturerName(String ManufacturerName) {
    this.ManufacturerName = ManufacturerName;
  }

  public String getManufacturerName() {
    return ManufacturerName;
  }

  public void setBuiltTime(Date BuiltTime) {
    this.BuiltTime = BuiltTime;
  }

  public Date getBuiltTime() {
    return BuiltTime;
  }

  public void setDeviceInfos(List<DeviceInfos> DeviceInfos) {
    this.DeviceInfos = DeviceInfos;
  }

  public List<DeviceInfos> getDeviceInfos() {
    return DeviceInfos;
  }

}