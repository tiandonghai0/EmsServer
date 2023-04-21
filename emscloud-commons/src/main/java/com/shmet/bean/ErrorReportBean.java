package com.shmet.bean;

import java.io.Serializable;
import java.util.List;

public class ErrorReportBean implements Serializable {

  private static final long serialVersionUID = 8891920483923437311L;
  Long timestamp;
  Long acconeId;
  List<ErrorReportItem> exceptions;

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getAcconeId() {
    return acconeId;
  }

  public void setAcconeId(Long acconeId) {
    this.acconeId = acconeId;
  }

  public List<ErrorReportItem> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<ErrorReportItem> exceptions) {
    this.exceptions = exceptions;
  }

}
