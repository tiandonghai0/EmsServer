package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

public class BaseChargeData {
  @TableId(type = IdType.AUTO)
  public Integer id;
  //峰时段的值
  @TableField(value = "feng_val")
  private Double fengVal;
  //平时段的值
  @TableField(value = "ping_val")
  private Double pingVal;
  //尖峰时段的值
  @TableField(value = "jfeng_val")
  private Double jfengVal;
  //谷时段的值
  @TableField(value = "gu_val")
  private Double guVal;
  //峰时段的电价
  @TableField(value = "feng_price")
  private Double fengPrice;
  //平时段的电价
  @TableField(value = "ping_price")
  private Double pingPrice;
  //尖峰时段电价
  @TableField(value = "jfeng_price")
  private Double jfengPrice;
  //谷时段的电价
  @TableField(value = "gu_price")
  private Double guPrice;
  //峰时段电费
  @TableField(value = "feng_period_price")
  private Double fengPeriodPrice;
  //平时段电费
  @TableField(value = "ping_period_price")
  private Double pingPeriodPrice;
  //谷时段电费
  @TableField(value = "gu_period_price")
  private Double guPeriodPrice;
  //尖峰时段电费
  @TableField(value = "jfeng_period_price")
  private Double jfengPeriodPrice;
  //subProjectId
  @TableField(value = "sub_project_id")
  private Long subProjectId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Double getFengVal() {
    return fengVal;
  }

  public void setFengVal(Double fengVal) {
    this.fengVal = fengVal;
  }

  public Double getPingVal() {
    return pingVal;
  }

  public void setPingVal(Double pingVal) {
    this.pingVal = pingVal;
  }

  public Double getJfengVal() {
    return jfengVal;
  }

  public void setJfengVal(Double jfengVal) {
    this.jfengVal = jfengVal;
  }

  public Double getGuVal() {
    return guVal;
  }

  public void setGuVal(Double guVal) {
    this.guVal = guVal;
  }

  public Double getFengPrice() {
    return fengPrice;
  }

  public void setFengPrice(Double fengPrice) {
    this.fengPrice = fengPrice;
  }

  public Double getPingPrice() {
    return pingPrice;
  }

  public void setPingPrice(Double pingPrice) {
    this.pingPrice = pingPrice;
  }

  public Double getJfengPrice() {
    return jfengPrice;
  }

  public void setJfengPrice(Double jfengPrice) {
    this.jfengPrice = jfengPrice;
  }

  public Double getGuPrice() {
    return guPrice;
  }

  public void setGuPrice(Double guPrice) {
    this.guPrice = guPrice;
  }

  public Long getSubProjectId() {
    return subProjectId;
  }

  public void setSubProjectId(Long subProjectId) {
    this.subProjectId = subProjectId;
  }

  public Double getFengPeriodPrice() {
    return fengPeriodPrice;
  }

  public void setFengPeriodPrice(Double fengPeriodPrice) {
    this.fengPeriodPrice = fengPeriodPrice;
  }

  public Double getPingPeriodPrice() {
    return pingPeriodPrice;
  }

  public void setPingPeriodPrice(Double pingPeriodPrice) {
    this.pingPeriodPrice = pingPeriodPrice;
  }

  public Double getGuPeriodPrice() {
    return guPeriodPrice;
  }

  public void setGuPeriodPrice(Double guPeriodPrice) {
    this.guPeriodPrice = guPeriodPrice;
  }

  public Double getJfengPeriodPrice() {
    return jfengPeriodPrice;
  }

  public void setJfengPeriodPrice(Double jfengPeriodPrice) {
    this.jfengPeriodPrice = jfengPeriodPrice;
  }
}
