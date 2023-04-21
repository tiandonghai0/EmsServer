package com.shmet.entity.mongo.field;

public class Strategy {
  private double greater;
  private double less;
  private int type;
  private double temperature;
  private int status;

  public void setGreater(double greater) {
    this.greater = greater;
  }

  public double getGreater() {
    return greater;
  }

  public void setLess(double less) {
    this.less = less;
  }

  public double getLess() {
    return less;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

}