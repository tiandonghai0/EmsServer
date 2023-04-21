package com.shmet.vo;

import com.shmet.entity.mysql.gen.ChargeListData;

/**
 * @author
 */
public class ChargeListDataVo extends ChargeListData {
  private String fengC;
  private String guC;
  private String pingC;
  private String pingVal;

  public void setFengC(String fengC) {
    this.fengC = fengC;
  }

  public void setGuC(String guC) {
    this.guC = guC;
  }

  public void setPingC(String pingC) {
    this.pingC = pingC;
  }

  public void setPingVal(String pingVal) {
    this.pingVal = pingVal;
  }

  public String getFengC() {
    return fengC;
  }

  public String getGuC() {
    return guC;
  }

  public String getPingC() {
    return pingC;
  }

  public String getPingVal() {
    return pingVal;
  }
}
