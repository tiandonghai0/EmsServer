package com.shmet.enums;

public enum PcsTagCodeEnum {
  CWS("CWS", "工作状态"), OOGS("OOGS", "并离网模式"),
  IES("IES", "充放电状态"), ATITC("ATITC", "机内温度(器件温度)"),
  FG("FG", "电网频率"), PF("PF", "功率因素"),
  AP("AP", "有功功率"), RP("RP", "无功功率"),
  QDI("QDI", "日充电量"), QDO("QDO", "日放电量"),
  QMI("QMI", "月充电量"), QMO("QMO", "月放电量"),
  QYI("QYI", "年充电量"), QYO("QYO", "年放电量"),
  UDC("UDC", "直流电压"), IDC("IDC", "直流电流"),
  DCPT("DCPT", "直流功率"), IACA("IACA", "A相电流"),
  IACB("IACB", "B相电流"), IACC("IACC", "C相电流"),
  UACAB("UACAB", "AB线电压"), UACBC("UACBC", "BC线电压"),
  UACCA("UACCA", "CA线电压"), DS("DS", "设备状态"),
  C_MAX_V("C_MAX_V", "单体最高电压"), C_MIN_V("C_MIN_V", "单体最低电压"),
  PE1("PE1", "直流过压保护"), PE2("PE2", "直流欠压保护"),
  PE3("PE3", "直流过流保护"), PE4("PE4", "直流反接保护"),
  PE5("PE5", "接地保护"), PE6("PE6", "过温保护"),
  PE7("PE7", "交流过压保护"), PE8("PE8", "交流欠压保护"),
  PE9("PE9", "交流过流保护"), PE10("PE10", "交流反序保护"),
  PE11("PE11", "浪涌保护"), PE12("PE12", "孤岛保护"),
  PE13("PE13", "频率异常告警");
  private final String tagCode;
  private final String tagName;

  PcsTagCodeEnum(String tagCode, String tagName) {
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
