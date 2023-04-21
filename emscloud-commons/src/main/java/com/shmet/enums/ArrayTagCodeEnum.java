package com.shmet.enums;

public enum ArrayTagCodeEnum {
  CPAE("CPAE"),
  CRAE("CRAE"),
  CPRE("CPRE"),
  CRRE("CRRE"),
  RB("RB"),
  DB("DB"),
  RAEOTD("RAEOTD"),
  RREOTD("RREOTD"),
  EPI("EPI"),
  EPE("EPE"),
  QTI("QTI"),
  QTO("QTO"),
  PREOTD("PREOTD"),
  PAEOTD("PAEOTD");

  private final String tagCode;

  ArrayTagCodeEnum(String tagCode) {
    this.tagCode = tagCode;
  }

  public String getTagCode() {
    return tagCode;
  }

  public static boolean match(String key) {
    for (ArrayTagCodeEnum value : ArrayTagCodeEnum.values()) {
      if (value.tagCode.equals(key)) {
        return true;
      }
    }
    return false;
  }

}
