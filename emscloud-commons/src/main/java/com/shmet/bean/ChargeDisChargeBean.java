package com.shmet.bean;

import java.io.Serializable;

public class ChargeDisChargeBean implements Serializable {
  private static final long serialVersionUID = 4434990039844628163L;

  Integer sendNo;
  // 1：communication; 2：web
  Integer senderId;
  String eventId;
  Integer status;
  Integer esNo;//储能编号
  Double p;
  Integer iDo;
  Long acconeId;
  String userName;
  // 1:充电, 2:放电, 3:停止
  Integer chargeType;
  //1:充电, 2:放电, 3:停止
  Integer lastChargeType;

  Double lastSoc;

  String chargeEPXTag;
  String dischargeEPXTag;


  public Integer getEsNo() {
    return esNo;
  }

  public void setEsNo(Integer esNo) {
    this.esNo = esNo;
  }

  public Integer getIDo() {
    return iDo;
  }

  public void setIDo(Integer iDo) {
    this.iDo = iDo;
  }


  /**
   * @return the sendNo
   */
  public Integer getSendNo() {
    return sendNo;
  }

  /**
   * @param sendNo the sendNo to set
   */
  public void setSendNo(Integer sendNo) {
    this.sendNo = sendNo;
  }

  /**
   * @return the status
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(Integer status) {
    this.status = status;
  }

  /**
   * @return the p
   */
  public Double getP() {
    return p;
  }

  /**
   * @param p the p to set
   */
  public void setP(Double p) {
    this.p = p;
  }

  /**
   * @return the acconeId
   */
  public Long getAcconeId() {
    return acconeId;
  }

  /**
   * @param acconeId the acconeId to set
   */
  public void setAcconeId(Long acconeId) {
    this.acconeId = acconeId;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * @return the chargeType
   */
  public Integer getChargeType() {
    return chargeType;
  }

  /**
   * @param chargeType the chargeType to set
   */
  public void setChargeType(Integer chargeType) {
    this.chargeType = chargeType;
  }

  /**
   * @return the lastChargeType
   */
  public Integer getLastChargeType() {
    return lastChargeType;
  }

  /**
   * @param lastChargeType the lastChargeType to set
   */
  public void setLastChargeType(Integer lastChargeType) {
    this.lastChargeType = lastChargeType;
  }

  /**
   * @return the lastSoc
   */
  public Double getLastSoc() {
    return lastSoc;
  }

  /**
   * @param lastSoc the lastSoc to set
   */
  public void setLastSoc(Double lastSoc) {
    this.lastSoc = lastSoc;
  }

  /**
   * @return the senderId
   */
  public Integer getSenderId() {
    return senderId;
  }

  /**
   * @param senderId the senderId to set
   */
  public void setSenderId(Integer senderId) {
    this.senderId = senderId;
  }

  /**
   * @return the chargeEPXTag
   */
  public String getChargeEPXTag() {
    return chargeEPXTag;
  }

  /**
   * @param chargeEPXTag the chargeEPXTag to set
   */
  public void setChargeEPXTag(String chargeEPXTag) {
    this.chargeEPXTag = chargeEPXTag;
  }

  /**
   * @return the dischargeEPXTag
   */
  public String getDischargeEPXTag() {
    return dischargeEPXTag;
  }

  /**
   * @param dischargeEPXTag the dischargeEPXTag to set
   */
  public void setDischargeEPXTag(String dischargeEPXTag) {
    this.dischargeEPXTag = dischargeEPXTag;
  }

  /**
   * @return the eventId
   */
  public String getEventId() {
    return eventId;
  }

  /**
   * @param eventId the eventId to set
   */
  public void setEventId(String eventId) {
    this.eventId = eventId;
  }


}
