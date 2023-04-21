package com.shmet.bean;

public class DBean {
  public Integer f;//开始时间
  public Integer t;//结束时间
  public Double p;//功率，负数充电、正数放电，0停机

  public Integer getF() {
    return f;
  }

  public void setF(Integer f) {
    this.f = f;
  }

  public Integer getT() {
    return t;
  }

  public void setT(Integer t) {
    this.t = t;
  }

  public Double getP() {
    return p;
  }

  public void setP(Double p) {
    this.p = p;
  }
}
