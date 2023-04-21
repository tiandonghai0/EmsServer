package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName(value = "t_accone")
public class Accone {
  @TableField(value = "accone_id")
  private Long acconeId;
  @TableField(value = "sub_project_id")
  private Long subProjectId;
  @TableField(value = "accone_sn")
  private String acconeSn;
  private String ver;
  private Integer status;
  @TableField(value = "running_status")
  private Integer runningStatus;
  @TableField(value = "accone_name")
  private String acconeName;
  @TableField(value = "create_date")
  private Date createDate;
  @TableField(value = "update_date")
  private Date updateDate;
  @TableField(value = "create_by")
  private Integer createBy;
  @TableField(value = "update_by")
  private Integer updateBy;
  private String iccid;

  public Long getAcconeId() {
    return acconeId;
  }

  public void setAcconeId(Long acconeId) {
    this.acconeId = acconeId;
  }

  public Long getSubProjectId() {
    return subProjectId;
  }

  public void setSubProjectId(Long subProjectId) {
    this.subProjectId = subProjectId;
  }

  public String getAcconeSn() {
    return acconeSn;
  }

  public void setAcconeSn(String acconeSn) {
    this.acconeSn = acconeSn;
  }

  public String getVer() {
    return ver;
  }

  public void setVer(String ver) {
    this.ver = ver;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getRunningStatus() {
    return runningStatus;
  }

  public void setRunningStatus(Integer runningStatus) {
    this.runningStatus = runningStatus;
  }

  public String getAcconeName() {
    return acconeName;
  }

  public void setAcconeName(String acconeName) {
    this.acconeName = acconeName;
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

  public String getIccid() {
    return iccid;
  }

  public void setIccid(String iccid) {
    this.iccid = iccid;
  }
}
