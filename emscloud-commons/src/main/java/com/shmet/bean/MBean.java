package com.shmet.bean;

public class MBean {
  public Integer mS;//开始月
  public Integer mE;//结束月

  public MBean() {
  }

  public MBean(Integer _mS, Integer _mE) {
    this.mS = _mS;
    this.mE = _mE;
  }

  public Integer getmS() {
    return mS;
  }

  public void setmS(Integer mS) {
    this.mS = mS;
  }

  public Integer getmE() {
    return mE;
  }

  public void setmE(Integer mE) {
    this.mE = mE;
  }

}
