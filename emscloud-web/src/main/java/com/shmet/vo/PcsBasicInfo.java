package com.shmet.vo;

/**
 * @author
 */
public class PcsBasicInfo {
  //运行状态
  private int runStatus;
  //运行时间
  private String runTime;
  //控制状态
  private int controlStatus;
  //有功功率
  private Double userfulP;
  //器件温度
  private Double widgetTemperature;

  public int getRunStatus() {
    return runStatus;
  }

  public void setRunStatus(int runStatus) {
    this.runStatus = runStatus;
  }

  public String getRunTime() {
    return runTime;
  }

  public void setRunTime(String runTime) {
    this.runTime = runTime;
  }

  public int getControlStatus() {
    return controlStatus;
  }

  public void setControlStatus(int controlStatus) {
    this.controlStatus = controlStatus;
  }

  public Double getUserfulP() {
    return userfulP;
  }

  public void setUserfulP(Double userfulP) {
    this.userfulP = userfulP;
  }

  public Double getWidgetTemperature() {
    return widgetTemperature;
  }

  public void setWidgetTemperature(Double widgetTemperature) {
    this.widgetTemperature = widgetTemperature;
  }
}
