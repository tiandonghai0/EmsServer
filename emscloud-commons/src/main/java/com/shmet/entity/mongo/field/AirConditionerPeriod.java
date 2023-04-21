package com.shmet.entity.mongo.field;

import java.util.List;

public class AirConditionerPeriod {
  private List<AirConditioner> airConditioner;
  private int airConditionerFrequency;
  private double airMaxHumidity;
  private double hot;
  private double cool;
  private double backValue;

  public double getHot() {
    return hot;
  }

  public void setHot(double hot) {
    this.hot = hot;
  }

  public double getCool() {
    return cool;
  }

  public void setCool(double cool) {
    this.cool = cool;
  }

  public double getBackValue() {
    return backValue;
  }

  public void setBackValue(double backValue) {
    this.backValue = backValue;
  }

  public void setAirConditioner(List<AirConditioner> airConditioner) {
    this.airConditioner = airConditioner;
  }

  public List<AirConditioner> getAirConditioner() {
    return airConditioner;
  }

  public void setAirConditionerFrequency(int airConditionerFrequency) {
    this.airConditionerFrequency = airConditionerFrequency;
  }

  public int getAirConditionerFrequency() {
    return airConditionerFrequency;
  }

  public double getAirMaxHumidity() {
    return airMaxHumidity;
  }

  public void setAirMaxHumidity(double airMaxHumidity) {
    this.airMaxHumidity = airMaxHumidity;
  }
}
