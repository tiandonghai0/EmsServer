package com.shmet.vo.req;

/**
 * @author
 */
public class OpLogConditionQuery {
  private Integer pageNum = 1;
  private Integer pageSize = 10;
  private String opPlatform;
  private String account;
  private Integer logType;
  //格式只支持 yyyy-MM-dd
  private String opTime;

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getOpPlatform() {
    return opPlatform;
  }

  public void setOpPlatform(String opPlatform) {
    this.opPlatform = opPlatform;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public Integer getLogType() {
    return logType;
  }

  public void setLogType(Integer logType) {
    this.logType = logType;
  }

  public String getOpTime() {
    return opTime;
  }

  public void setOpTime(String opTime) {
    this.opTime = opTime;
  }
}
