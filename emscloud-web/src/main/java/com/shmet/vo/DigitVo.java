package com.shmet.vo;

/**
 * @author
 * 组串返回数据VO
 */
public class DigitVo {
  private Long digitNo;
  private Double status;
  private Double soc;
  private Double soh;
  //最高电压
  private Double maxU;
  //最低电压
  private Double minU;
  //最高温度
  private Double maxT;
  //最大SOC
  private Double maxSoc;
  //最大SOH
  private Double maxSoh;

  public DigitVo() {
  }

  public DigitVo(Double soc, Double soh, Double maxU, Double minU, Double maxT, Double maxSoc, Double maxSoh) {
    this.soc = soc;
    this.soh = soh;
    this.maxU = maxU;
    this.minU = minU;
    this.maxT = maxT;
    this.maxSoc = maxSoc;
    this.maxSoh = maxSoh;
  }

  public DigitVo(Double status, Double soc, Double soh, Double maxU, Double minU, Double maxT, Double maxSoc, Double maxSoh) {
    this.status = status;
    this.soc = soc;
    this.soh = soh;
    this.maxU = maxU;
    this.minU = minU;
    this.maxT = maxT;
    this.maxSoc = maxSoc;
    this.maxSoh = maxSoh;
  }

  public Long getDigitNo() {
    return digitNo;
  }

  public void setDigitNo(Long digitNo) {
    this.digitNo = digitNo;
  }

  public Double getStatus() {
    return status;
  }

  public void setStatus(Double status) {
    this.status = status;
  }

  public Double getSoc() {
    return soc;
  }

  public void setSoc(Double soc) {
    this.soc = soc;
  }

  public Double getSoh() {
    return soh;
  }

  public void setSoh(Double soh) {
    this.soh = soh;
  }

  public Double getMaxU() {
    return maxU;
  }

  public void setMaxU(Double maxU) {
    this.maxU = maxU;
  }

  public Double getMinU() {
    return minU;
  }

  public void setMinU(Double minU) {
    this.minU = minU;
  }

  public Double getMaxT() {
    return maxT;
  }

  public void setMaxT(Double maxT) {
    this.maxT = maxT;
  }

  public Double getMaxSoc() {
    return maxSoc;
  }

  public void setMaxSoc(Double maxSoc) {
    this.maxSoc = maxSoc;
  }

  public Double getMaxSoh() {
    return maxSoh;
  }

  public void setMaxSoh(Double maxSoh) {
    this.maxSoh = maxSoh;
  }

  @Override
  public String toString() {
    return "DigitVo{" +
        "soc=" + soc +
        ", soh=" + soh +
        ", maxU=" + maxU +
        ", minU=" + minU +
        ", maxT=" + maxT +
        ", maxSoc=" + maxSoc +
        ", maxSoh=" + maxSoh +
        '}';
  }
}
