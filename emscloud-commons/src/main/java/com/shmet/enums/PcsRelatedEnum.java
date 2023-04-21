package com.shmet.enums;

public enum PcsRelatedEnum {
  P("P", "功率"), IA("IA", "A相电流"),
  IB("IB", "B相电流"), IC("IC", "C相电流"),
  UAB("UAB", "AB线电压"), UAC("UAC", "CA线电压"),
  UBC("UBC", "BC线电压");
  private final String tagCode;
  private final String tagName;

  PcsRelatedEnum(String tagCode, String tagName) {
    this.tagCode = tagCode;
    this.tagName = tagName;
  }

  public String getTagName() {
    return tagName;
  }

  public String getTagCode() {
    return tagCode;
  }
}
