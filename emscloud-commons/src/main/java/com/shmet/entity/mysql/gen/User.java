package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "t_user")
public class User implements Serializable {
  private String account;
  private String password;
  private String name;
  private String email;
  @TableField(value = "create_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createDate;
  @TableField(value = "update_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateDate;
  @TableField(value = "secondary_password")
  private String secondaryPassword;
  @TableField(value = "project_no")
  private String projectNo;

  public void setAccount(String account) {
    this.account = account;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public String getAccount() {
    return account;
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public String getSecondaryPassword() {
    return secondaryPassword;
  }

  public void setSecondaryPassword(String secondaryPassword) {
    this.secondaryPassword = secondaryPassword;
  }

  public String getProjectNo() {
    return projectNo;
  }

  public void setProjectNo(String projectNo) {
    this.projectNo = projectNo;
  }

  @Override
  public String toString() {
    return "User{" +
        "account='" + account + '\'' +
        ", password='" + password + '\'' +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", createDate=" + createDate +
        ", updateDate=" + updateDate +
        '}';
  }
}
