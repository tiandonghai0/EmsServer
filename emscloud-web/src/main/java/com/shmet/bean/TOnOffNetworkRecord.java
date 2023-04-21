package com.shmet.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "t_on_off_network_record")
public class TOnOffNetworkRecord implements Serializable {
  @TableId
  @TableField(value = "f_id")
  private Integer fId;
  @TableField(value = "f_device_no")
  private Long fDeviceNo;
  @TableField(value = "f_ammeter_device_no")
  private Long ammeterDeviceNo;
  @TableField(value = "f_sub_project_id")
  private Long fSubProjectId;
  @TableField(value = "f_off_time")
  private Long fOffTime;
  @TableField(value = "f_on_time")
  private Long fOnTime;
  @TableField(value = "f_create_time")
  private Date fCreateTime;
  @TableField(value = "f_update_time")
  private Date fUpdateTime;

  public void setfId(Integer fId) {
    this.fId = fId;
  }

  public void setfDeviceNo(Long fDeviceNo) {
    this.fDeviceNo = fDeviceNo;
  }

  public void setfSubProjectId(Long fSubProjectId) {
    this.fSubProjectId = fSubProjectId;
  }

  public void setfOffTime(Long fOffTime) {
    this.fOffTime = fOffTime;
  }

  public void setfOnTime(Long fOnTime) {
    this.fOnTime = fOnTime;
  }

  public void setfCreateTime(Date fCreateTime) {
    this.fCreateTime = fCreateTime;
  }

  public void setfUpdateTime(Date fUpdateTime) {
    this.fUpdateTime = fUpdateTime;
  }

  public Integer getfId() {
    return fId;
  }

  public Long getfDeviceNo() {
    return fDeviceNo;
  }

  public Long getfSubProjectId() {
    return fSubProjectId;
  }

  public Long getfOffTime() {
    return fOffTime;
  }

  public Long getfOnTime() {
    return fOnTime;
  }

  public Date getfCreateTime() {
    return fCreateTime;
  }

  public Date getfUpdateTime() {
    return fUpdateTime;
  }

  public void setAmmeterDeviceNo(Long ammeterDeviceNo) {
    this.ammeterDeviceNo = ammeterDeviceNo;
  }

  public Long getAmmeterDeviceNo() {
    return ammeterDeviceNo;
  }
}
