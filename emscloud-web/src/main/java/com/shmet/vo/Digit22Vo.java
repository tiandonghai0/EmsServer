package com.shmet.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Digit22Vo extends DigitVo {
  //tagCode SOCSMIN 电芯最低SOC
  private Double socsMin;
  //tagCode SOHSMIN 电芯最低SOH
  private Double sohsMin;
  //tagCode MAX_B 最高电压电芯编号
  private Double maxBNo;
  //tagCode MIN_B 最低电压电芯编号
  private Double minBNo;
  //tagCode MIN_SOC_B 最低SOC电芯编号
  private Double minSocBNo;
  //tagCode MIN_SOH_B 最低SOH电芯编号
  private Double minSohBNo;
  //tagCode MAX_T 最高温度电芯编号
  private Double maxTNo;
  //状态字
  private Double WSTS;
  //报警字

  private Double WALM;
  //故障字

  private Double WFLT;
  //电流

  private Double I;
  //电压

  private Double U;
  //单体温度最小值

  private Double LBT;
  //最小温度电芯编号

  private Double NOLBT;
  //最小温度电芯编号

  private Double NOLSOC;

  public Digit22Vo(Double status, Double soc, Double soh, Double maxU, Double minU, Double maxT, Double maxSoc, Double maxSoh,
                   Double socsMin, Double sohsMin, Double maxBNo,
                   Double minBNo, Double minSocBNo, Double minSohBNo, Double maxTNo,
                   Double WSTS, Double WALM, Double WFLT, Double I, Double U,
                   Double LBT, Double NOLBT, Double NOLSOC) {
    super(status, soc, soh, maxU, minU, maxT, maxSoc, maxSoh);
    this.socsMin = socsMin;
    this.sohsMin = sohsMin;
    this.maxBNo = maxBNo;
    this.minBNo = minBNo;
    this.minSocBNo = minSocBNo;
    this.minSohBNo = minSohBNo;
    this.maxTNo = maxTNo;
    this.WSTS = WSTS;
    this.WALM = WALM;
    this.WFLT = WFLT;
    this.I = I;
    this.U = U;
    this.LBT = LBT;
    this.NOLBT = NOLBT;
    this.NOLSOC = NOLSOC;
  }

  public Double getSocsMin() {
    return socsMin;
  }

  public void setSocsMin(Double socsMin) {
    this.socsMin = socsMin;
  }

  public Double getSohsMin() {
    return sohsMin;
  }

  public void setSohsMin(Double sohsMin) {
    this.sohsMin = sohsMin;
  }

  public Double getMaxBNo() {
    return maxBNo;
  }

  public void setMaxBNo(Double maxBNo) {
    this.maxBNo = maxBNo;
  }

  public Double getMinBNo() {
    return minBNo;
  }

  public void setMinBNo(Double minBNo) {
    this.minBNo = minBNo;
  }

  public Double getMinSocBNo() {
    return minSocBNo;
  }

  public void setMinSocBNo(Double minSocBNo) {
    this.minSocBNo = minSocBNo;
  }

  public Double getMinSohBNo() {
    return minSohBNo;
  }

  public void setMinSohBNo(Double minSohBNo) {
    this.minSohBNo = minSohBNo;
  }

  public Double getMaxTNo() {
    return maxTNo;
  }

  public void setMaxTNo(Double maxTNo) {
    this.maxTNo = maxTNo;
  }

  @JsonProperty(value = "WSTS")
  public Double getWSTS() {
    return WSTS;
  }

  public void setWSTS(Double WSTS) {
    this.WSTS = WSTS;
  }

  @JsonProperty(value = "WALM")
  public Double getWALM() {
    return WALM;
  }

  public void setWALM(Double WALM) {
    this.WALM = WALM;
  }

  @JsonProperty(value = "WFLT")
  public Double getWFLT() {
    return WFLT;
  }

  public void setWFLT(Double WFLT) {
    this.WFLT = WFLT;
  }

  @JsonProperty(value = "I")
  public Double getI() {
    return I;
  }

  public void setI(Double i) {
    I = i;
  }

  @JsonProperty(value = "U")
  public Double getU() {
    return U;
  }

  public void setU(Double u) {
    U = u;
  }

  @JsonProperty(value = "LBT")
  public Double getLBT() {
    return LBT;
  }

  public void setLBT(Double LBT) {
    this.LBT = LBT;
  }

  @JsonProperty(value = "NOLBT")
  public Double getNOLBT() {
    return NOLBT;
  }

  public void setNOLBT(Double NOLBT) {
    this.NOLBT = NOLBT;
  }

  @JsonProperty(value = "NOLSOC")
  public Double getNOLSOC() {
    return NOLSOC;
  }

  public void setNOLSOC(Double NOLSOC) {
    this.NOLSOC = NOLSOC;
  }
}
