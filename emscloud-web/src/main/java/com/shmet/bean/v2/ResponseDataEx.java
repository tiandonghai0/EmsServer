package com.shmet.bean.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author
 */
public class ResponseDataEx {
  private Integer customerId;
  private String customerNo;
  @JsonProperty("logo")
  private String customerlogo;
  private String city;
  private String customName;
  private String projectName;
  private List<ConfigEx> configs;

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getCustomerNo() {
    return customerNo;
  }

  public void setCustomerNo(String customerNo) {
    this.customerNo = customerNo;
  }

  public List<ConfigEx> getConfigs() {
    return configs;
  }

  public void setConfigs(List<ConfigEx> configs) {
    this.configs = configs;
  }

  public String getCustomerlogo() {
    return customerlogo;
  }

  public void setCustomerlogo(String customerlogo) {
    this.customerlogo = customerlogo;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCustomName() {
    return customName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
}
