package com.shmet.bean.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author
 * 登录返回信息配置扩展实体
 */
public class ConfigEx {
  private Long subprojectId;
  private Long storageElec;
  private List<Long> cityElec;
  private Integer showType;
  @JsonProperty("pcsDevices")
  //@JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaseModel> pcsDeviceNos;
  @JsonProperty("bmsDevices")
  //@JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaseModel> bmsDeviceNos;
  @JsonProperty("airDevices")
  //@JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaseModel> airDeviceNos;
  @JsonProperty("sunPcsDevices")
  //@JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaseModel> sunPcsDeviceNos;

  @JsonProperty("tctrDevices")
  private List<BaseModel> tctrDevices;



  @JsonProperty("cityElecName")
  //@JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaseModel> cityElecName;

  @JsonProperty("adPcsDeviceNos")
  private List<BaseModel>  adPcsDeviceNos;

  public Long getSubprojectId() {
    return subprojectId;
  }

  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  public List<BaseModel> getPcsDeviceNos() {
    return pcsDeviceNos;
  }

  public void setPcsDeviceNos(List<BaseModel> pcsDeviceNos) {
    this.pcsDeviceNos = pcsDeviceNos;
  }

  public List<BaseModel> getBmsDeviceNos() {
    return bmsDeviceNos;
  }

  public void setBmsDeviceNos(List<BaseModel> bmsDeviceNos) {
    this.bmsDeviceNos = bmsDeviceNos;
  }

  public List<BaseModel> getAirDeviceNos() {
    return airDeviceNos;
  }

  public void setAirDeviceNos(List<BaseModel> airDeviceNos) {
    this.airDeviceNos = airDeviceNos;
  }

  public List<BaseModel> getSunPcsDeviceNos() {
    return sunPcsDeviceNos;
  }

  public void setSunPcsDeviceNos(List<BaseModel> sunPcsDeviceNos) {
    this.sunPcsDeviceNos = sunPcsDeviceNos;
  }

  public Long getStorageElec() {
    return storageElec;
  }

  public void setStorageElec(Long storageElec) {
    this.storageElec = storageElec;
  }

  public List<Long> getCityElec() {
    return cityElec;
  }

  public void setCityElec(List<Long> cityElec) {
    this.cityElec = cityElec;
  }

  public List<BaseModel> getCityElecName() {
    return cityElecName;
  }

  public void setCityElecName(List<BaseModel> cityElecName) {
    this.cityElecName = cityElecName;
  }

  public List<BaseModel> getTctrDevices() {
    return tctrDevices;
  }

  public void setTctrDevices(List<BaseModel> tctrDevices) {
    this.tctrDevices = tctrDevices;
  }

  public Integer getShowType() {
    return showType;
  }

  public void setShowType(Integer showType) {
    this.showType = showType;
  }


  public List<BaseModel> getAdPcsDeviceNos() {
    return adPcsDeviceNos;
  }

  public void setAdPcsDeviceNos(List<BaseModel> adPcsDeviceNos) {
    this.adPcsDeviceNos = adPcsDeviceNos;
  }

  public static BaseModel build(Long id, String name) {
    return new BaseModel(id, name);
  }

  public static class BaseModel {
    private Long id;
    private String name;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public BaseModel() {
    }

    public BaseModel(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }
}
