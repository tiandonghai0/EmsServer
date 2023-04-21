package com.shmet.bean;

import java.util.Date;

public class Lock {
  long timestamp;
  
  public Lock () {
    this.timestamp = (new Date()).getTime();
  }

  /**
   * @return the timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
}
