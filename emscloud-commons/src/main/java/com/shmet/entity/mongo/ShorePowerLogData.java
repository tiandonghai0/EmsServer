package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shorePowerLogData")
@CompoundIndexes({  
  @CompoundIndex(name = "acconeId_deviceId_timestamp_idx", def = "{'acconeId':1, 'deviceId':1, 'timestamp': -1}")
})
public class ShorePowerLogData {
  @Id
  private String id;
  
  @NotNull
  private Long acconeId;
  
  @NotNull
  private Integer deviceId;
  
  @NotNull
  private Integer ssType;

  @NotNull 
  private Double currentEnergy;

  private Double startEnergy;
  
  private Double diff;
  
  @NotNull 
  private String dateTime;
  
  @NotNull 
  private Long timestamp;

  private Long startTime;
  
  @NotNull 
  private Integer projectId;
  
  @NotNull 
  private Long subProjectId;

  private String orderId;

  private String powerOutletId;

  public String getPowerOutletId() {
    return powerOutletId;
  }

  public void setPowerOutletId(String powerOutletId) {
    this.powerOutletId = powerOutletId;
  }


  
  static public String getCollectionName () {
    return "shorePowerLogData";
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
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
   * @return the currentEnergy
   */
  public Double getCurrentEnergy() {
    return currentEnergy;
  }

  /**
   * @param currentEnergy the currentEnergy to set
   */
  public void setCurrentEnergy(Double currentEnergy) {
    this.currentEnergy = currentEnergy;
  }


  /**
   * @return the dateTime
   */
  public String getDateTime() {
    return dateTime;
  }

  /**
   * @param dateTime the dateTime to set
   */
  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
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
   * @return the projectId
   */
  public Integer getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /**
   * @return the subProjectId
   */
  public Long getSubProjectId() {
    return subProjectId;
  }

  /**
   * @param subProjectId the subProjectId to set
   */
  public void setSubProjectId(Long subProjectId) {
    this.subProjectId = subProjectId;
  }

  /**
   * @return the ssType
   */
  public Integer getSsType() {
    return ssType;
  }

  /**
   * @param ssType the ssType to set
   */
  public void setSsType(Integer ssType) {
    this.ssType = ssType;
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

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Double getStartEnergy() {
    return startEnergy;
  }

  public void setStartEnergy(Double startEnergy) {
    this.startEnergy = startEnergy;
  }
}
