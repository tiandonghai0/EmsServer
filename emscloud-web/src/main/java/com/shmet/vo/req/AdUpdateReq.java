package com.shmet.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author
 */
public class AdUpdateReq {
  private Integer id;
  @JsonProperty(value = "berthTime")
  private String berthTime;
  @JsonProperty(value = "departureTime")
  private String departureTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getBerthTime() {
    return berthTime;
  }

  public void setBerthTime(String berthTime) {
    this.berthTime = berthTime;
  }

  public String getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(String departureTime) {
    this.departureTime = departureTime;
  }
}
