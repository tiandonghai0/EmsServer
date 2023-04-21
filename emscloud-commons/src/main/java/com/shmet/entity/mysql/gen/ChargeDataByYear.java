package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "t_chargedata_byyear")
public class ChargeDataByYear extends BaseChargeData {
  //具体哪一月 格式为 yyyy-MM
  private String month;
  //某一月各时段用电量的总和
  @TableField(value = "month_sum")
  private Double monthSum;
  //某一月的电费
  @TableField(value = "month_electric_price")
  private Double monthElectricPrice;

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public Double getMonthSum() {
    return monthSum;
  }

  public void setMonthSum(Double monthSum) {
    this.monthSum = monthSum;
  }

  public Double getMonthElectricPrice() {
    return monthElectricPrice;
  }

  public void setMonthElectricPrice(Double monthElectricPrice) {
    this.monthElectricPrice = monthElectricPrice;
  }
}
