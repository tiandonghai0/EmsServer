package com.shmet.vo;

/**
 * @author
 * 光伏接口返回数据Vo
 */
public class PhotovoltaicVo {
  //光伏类型
  private String ptaicType;
  //运行状态
  private String runStatus;
  //运行时间
  private String runTime;
  //发电功率
  private Double p;
  //电压
  private Double u;
  //电流
  private Double i;
  //日发电量
  private Double powerelec;

  public String getPtaicType() {
    return ptaicType;
  }

  public void setPtaicType(String ptaicType) {
    this.ptaicType = ptaicType;
  }

  public String getRunStatus() {
    return runStatus;
  }

  public void setRunStatus(String runStatus) {
    this.runStatus = runStatus;
  }

  public String getRunTime() {
    return runTime;
  }

  public void setRunTime(String runTime) {
    this.runTime = runTime;
  }

  public Double getP() {
    return p;
  }

  public void setP(Double p) {
    this.p = p;
  }

  public Double getU() {
    return u;
  }

  public void setU(Double u) {
    this.u = u;
  }

  public Double getI() {
    return i;
  }

  public void setI(Double i) {
    this.i = i;
  }

  public Double getPowerelec() {
    return powerelec;
  }

  public void setPowerelec(Double powerelec) {
    this.powerelec = powerelec;
  }

  @Override
  public String toString() {
    return "photovoltaicVo{" +
        "runStatus='" + runStatus + '\'' +
        ", runTime='" + runTime + '\'' +
        ", p=" + p +
        ", u=" + u +
        ", i=" + i +
        ", powerelec=" + powerelec +
        '}';
  }
}
