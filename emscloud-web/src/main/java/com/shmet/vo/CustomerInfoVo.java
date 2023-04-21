package com.shmet.vo;

/**
 * @author
 */
public class CustomerInfoVo {
  private String contactName;
  private String mobile;
  //经纬度信息
  private String longitude;
  private String latitude;
  //地址信息
  private String addr;
  //运行周期(天数)
  private long runDay;
  //放电量
  private Double dayEpe;
  //充电量
  private Double dayEpi;
  //发电量
  private Double powerPlant = 0d;

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public long getRunDay() {
    return runDay;
  }

  public void setRunDay(long runDay) {
    this.runDay = runDay;
  }

  public Double getDayEpe() {
    return dayEpe;
  }

  public void setDayEpe(Double dayEpe) {
    this.dayEpe = dayEpe;
  }

  public Double getDayEpi() {
    return dayEpi;
  }

  public void setDayEpi(Double dayEpi) {
    this.dayEpi = dayEpi;
  }

  public Double getPowerPlant() {
    return powerPlant;
  }

  public void setPowerPlant(Double powerPlant) {
    this.powerPlant = powerPlant;
  }
}
