package com.shmet.bean;

import org.springframework.format.annotation.DateTimeFormat;

public class EHang365StartStopReqBean {
	private String equipmentId;
	
	private Integer order;

	private String orderId;

	private String powerOutletId;

	private String userId;

  private String shipId;

  private String shipName;


  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String timestamp;

    /**
     * @return the equipmentId
     */
    public String getEquipmentId() {
      return equipmentId;
    }

    /**
     * @param equipmentId the equipmentId to set
     */
    public void setEquipmentId(String equipmentId) {
      this.equipmentId = equipmentId;
    }

    /**
     * @return the order
     */
    public Integer getOrder() {
      return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Integer order) {
      this.order = order;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
      return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getPowerOutletId() {
    return powerOutletId;
  }

  public void setPowerOutletId(String powerOutletId) {
    this.powerOutletId = powerOutletId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getShipId() {
    return shipId;
  }

  public void setShipId(String shipId) {
    this.shipId = shipId;
  }

  public String getShipName() {
    return shipName;
  }

  public void setShipName(String shipName) {
    this.shipName = shipName;
  }
}