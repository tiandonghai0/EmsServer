package com.shmet.enums;

public enum AlarmEnum {
  ALARM(1, "告警"), ONELEVEL_ALARM(2, "一级告警"),
  TWOLEVEL_ALARM(3, "二级告警"), THREELEVEl_ALARM(4, "三级告警"),
  FAULT(5, "故障");

  int alarmCode;
  String alarmLevel;

  AlarmEnum(int alarmCode, String alarmLevel) {
    this.alarmCode = alarmCode;
    this.alarmLevel = alarmLevel;
  }

  public int getAlarmCode() {
    return alarmCode;
  }

  public String getAlarmLevel() {
    return alarmLevel;
  }

  public static String matchAlarmCode(int d) {
    for (AlarmEnum value : AlarmEnum.values()) {
      if (value.getAlarmCode() == d) {
        return value.getAlarmLevel();
      }
    }
    return "";
  }
}
