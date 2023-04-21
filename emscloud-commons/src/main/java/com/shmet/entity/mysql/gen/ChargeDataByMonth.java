package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "t_chargedata_bymonth")
public class ChargeDataByMonth extends BaseChargeData {
  //具体哪一天 格式为 yyyy-MM-dd
  private String date;
  //某一天各时段用电量的总和
  @TableField(value = "day_sum")
  private Double daySum;
  //某一天的电费
  @TableField(value = "day_electric_price")
  private Double dayElectricPrice;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Double getDaySum() {
    return daySum;
  }

  public void setDaySum(Double daySum) {
    this.daySum = daySum;
  }

  public Double getDayElectricPrice() {
    return dayElectricPrice;
  }

  public void setDayElectricPrice(Double dayElectricPrice) {
    this.dayElectricPrice = dayElectricPrice;
  }
}
