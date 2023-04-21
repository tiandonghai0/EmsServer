package com.shmet.vo;

/**
 * @author
 */
public class RunStatusVo {
  //电池容量
  private Double batteryCapacity = 0d;
  //功率
  private Double p = 0d;
  //电压
  private Double voltage = 0d;
  //电流
  private Double ia = 0d;
  //状态
  private Double status;
  //电量占比
  private Double electricCost = 0d;

  public Double getBatteryCapacity() {
    return batteryCapacity;
  }

  public void setBatteryCapacity(Double batteryCapacity) {
    this.batteryCapacity = batteryCapacity;
  }

  public Double getP() {
    return p;
  }

  public void setP(Double p) {
    this.p = p;
  }

  public Double getVoltage() {
    return voltage;
  }

  public void setVoltage(Double voltage) {
    this.voltage = voltage;
  }

  public Double getIa() {
    return ia;
  }

  public void setIa(Double ia) {
    this.ia = ia;
  }

  public Double getStatus() {
    return status;
  }

  public void setStatus(Double status) {
    this.status = status;
  }

  public double getElectricCost() {
    return electricCost;
  }

  public void setElectricCost(double electricCost) {
    this.electricCost = electricCost;
  }
}
