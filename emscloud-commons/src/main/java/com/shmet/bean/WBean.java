package com.shmet.bean;

import java.util.List;

public class WBean {
  public Integer wS;//开始周1-6是周一到周六
  public Integer wE;//结束周,0是周日
  public List<DBean> d;

  public Integer getwS() {
    return wS;
  }

  public void setwS(Integer wS) {
    this.wS = wS;
  }

  public Integer getwE() {
    return wE;
  }

  public void setwE(Integer wE) {
    this.wE = wE;
  }

  public List<DBean> getD() {
    return d;
  }

  public void setD(List<DBean> d) {
    this.d = d;
  }
}
