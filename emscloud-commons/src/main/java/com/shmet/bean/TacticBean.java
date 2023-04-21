package com.shmet.bean;

import java.util.List;

public class TacticBean {
  public List<MBean> m;//月
  public List<WBean> w;//周
  public List<MBean> getM() {
    return m;
  }

  public void setM(List<MBean> m) {
    this.m = m;
  }

  public List<WBean> getW() {
    return w;
  }

  public void setW(List<WBean> w) {
    this.w = w;
  }
}

