package com.shmet.bean;

import java.io.Serializable;
import java.util.Map;

public class SystemBootBean implements Serializable {
  
    private static final long serialVersionUID = 4434990039844628163L;
    Long strategyNo;
    Long acconeId;
    Integer sendNo;
    Map<String, Object> params;
    Integer status;
    Integer rstatus;
    String userName;

    public Long getStrategyNo() {
        return strategyNo;
    }

    public void setStrategyNo(Long strategyNo) {
        this.strategyNo = strategyNo;
    }

    public Integer getSendNo() {
        return sendNo;
    }

    public void setSendNo(Integer sendNo) {
        this.sendNo = sendNo;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public Integer getRstatus() {
        return rstatus;
    }

    public void setRstatus(Integer rstatus) {
        this.rstatus = rstatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
