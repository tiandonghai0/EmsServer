package com.shmet.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PostResponseBean {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean successed;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorCode;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMsg;

  public Boolean isSuccessed() {
    return successed;
  }

  public void setSuccessed(Boolean successed) {
    this.successed = successed;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
}
