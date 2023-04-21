package com.shmet.bean.foreign;

import java.util.Date;
import java.util.List;

public class DeviceInfos {

  private String ProjectID;
  private String PlatformID;
  private String DeviceID;
  private Date DeviceName;
  private int DeviceType;
  private double DeviceLng;
  private double DeviceLat;
  private int DeviceCapacity;
  private List<Double> DeviceVoltage;
  private List<Integer> DeviceFrequency;
  private String Remarks;

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

  public void setDeviceName(Date DeviceName) {
    this.DeviceName = DeviceName;
  }

  public Date getDeviceName() {
    return DeviceName;
  }

  public void setDeviceType(int DeviceType) {
    this.DeviceType = DeviceType;
  }

  public int getDeviceType() {
    return DeviceType;
  }

  public void setDeviceLng(double DeviceLng) {
    this.DeviceLng = DeviceLng;
  }

  public double getDeviceLng() {
    return DeviceLng;
  }

  public void setDeviceLat(double DeviceLat) {
    this.DeviceLat = DeviceLat;
  }

  public double getDeviceLat() {
    return DeviceLat;
  }

  public void setDeviceCapacity(int DeviceCapacity) {
    this.DeviceCapacity = DeviceCapacity;
  }

  public int getDeviceCapacity() {
    return DeviceCapacity;
  }

  public void setDeviceVoltage(List<Double> DeviceVoltage) {
    this.DeviceVoltage = DeviceVoltage;
  }

  public List<Double> getDeviceVoltage() {
    return DeviceVoltage;
  }

  public void setDeviceFrequency(List<Integer> DeviceFrequency) {
    this.DeviceFrequency = DeviceFrequency;
  }

  public List<Integer> getDeviceFrequency() {
    return DeviceFrequency;
  }

  public void setRemarks(String Remarks) {
    this.Remarks = Remarks;
  }

  public String getRemarks() {
    return Remarks;
  }

}