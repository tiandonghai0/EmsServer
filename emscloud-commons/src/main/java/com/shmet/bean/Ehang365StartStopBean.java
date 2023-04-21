package com.shmet.bean;

import java.io.Serializable;

public class Ehang365StartStopBean implements Serializable {

    /**
   * 
   */
  private static final long serialVersionUID = 84199545471859414L;
    Integer sendNo = 1;
    Integer status;
    Double ce;
    Double diff;
    Long acconeId;
    Integer deviceId;
    Long timestamp;
    String errorMsg;
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
     * @return the ce
     */
    public Double getCe() {
      return ce;
    }
    /**
     * @param ce the ce to set
     */
    public void setCe(Double ce) {
      this.ce = ce;
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
     * @return the deviceId
     */
    public Integer getDeviceId() {
      return deviceId;
    }
    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(Integer deviceId) {
      this.deviceId = deviceId;
    }
    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
      return timestamp;
    }
    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
    }
    /**
     * @return the diff
     */
    public Double getDiff() {
      return diff;
    }
    /**
     * @param diff the diff to set
     */
    public void setDiff(Double diff) {
      this.diff = diff;
    }
    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
      return errorMsg;
    }
    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
      this.errorMsg = errorMsg;
    }


}
