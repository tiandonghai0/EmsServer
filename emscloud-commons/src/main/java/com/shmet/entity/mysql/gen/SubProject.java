package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName(value = "t_sub_project")
public class SubProject {
  @TableId
  @TableField(value = "sub_project_id")
  private Long subProjectId;
  @TableField(value = "project_id")
  private Integer projectId;
  @TableField(value = "station_id")
  private Integer stationId;
  @TableField(value = "sub_project_name")
  private String subProjectName;
  @TableField(value = "create_date")
  private Date createDate;
  @TableField(value = "update_date")
  private Date updateDate;
  @TableField(value = "create_by")
  private Integer createBy;
  @TableField(value = "update_by")
  private Integer updateBy;
  @TableField(value = "total_capacity")
  private Double totalCapacity;
  @TableField(value = "battery_type")
  private Integer batteryType;

  public Long getSubProjectId() {
    return subProjectId;
  }

  public void setSubProjectId(Long subProjectId) {
    this.subProjectId = subProjectId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public Integer getStationId() {
    return stationId;
  }

  public void setStationId(Integer stationId) {
    this.stationId = stationId;
  }

  public String getSubProjectName() {
    return subProjectName;
  }

  public void setSubProjectName(String subProjectName) {
    this.subProjectName = subProjectName;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public Integer getCreateBy() {
    return createBy;
  }

  public void setCreateBy(Integer createBy) {
    this.createBy = createBy;
  }

  public Integer getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(Integer updateBy) {
    this.updateBy = updateBy;
  }

  public Double getTotalCapacity() {
    return totalCapacity;
  }

  public void setTotalCapacity(Double totalCapacity) {
    this.totalCapacity = totalCapacity;
  }

  public Integer getBatteryType() {
    return batteryType;
  }

  public void setBatteryType(Integer batteryType) {
    this.batteryType = batteryType;
  }
}
