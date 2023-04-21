package com.shmet.entity.mongo.field;

import java.util.List;

public class AirConditioner {
  private String name;
  private String deviceNo;
  private List<Month> month;
  private List<Strategy> strategy;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDeviceNo(String deviceNo) {
    this.deviceNo = deviceNo;
  }

  public String getDeviceNo() {
    return deviceNo;
  }

  public void setMonth(List<Month> month) {
    this.month = month;
  }

  public List<Month> getMonth() {
    return month;
  }

  public void setStrategy(List<Strategy> strategy) {
    this.strategy = strategy;
  }

  public List<Strategy> getStrategy() {
    return strategy;
  }

}