package com.shmet.entity.mongo;

public class AutoExamEntity {
  private Long subprojectId;
  private String prjname;
  // 0 代表正常 1 代表异常
  private Integer emuStatus;
  private String simStatus;
  private String simRemainRate;
  private String simEffectDate;
  // 0 代表正常 1 代表异常
  private Integer digitStatus = 0;
  // 0 代表正常 1 代表异常
  private Integer projectStatus;
  // 0 代表正常 1 代表异常
  private Integer alarmStatus;

  public Long getSubprojectId() {
    return subprojectId;
  }

  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  public String getPrjname() {
    return prjname;
  }

  public void setPrjname(String prjname) {
    this.prjname = prjname;
  }

  public Integer getEmuStatus() {
    return emuStatus;
  }

  public void setEmuStatus(Integer emuStatus) {
    this.emuStatus = emuStatus;
  }

  public String getSimStatus() {
    return simStatus;
  }

  public void setSimStatus(String simStatus) {
    this.simStatus = simStatus;
  }

  public String getSimRemainRate() {
    return simRemainRate;
  }

  public void setSimRemainRate(String simRemainRate) {
    this.simRemainRate = simRemainRate;
  }

  public String getSimEffectDate() {
    return simEffectDate;
  }

  public void setSimEffectDate(String simEffectDate) {
    this.simEffectDate = simEffectDate;
  }

  public Integer getDigitStatus() {
    return digitStatus;
  }

  public void setDigitStatus(Integer digitStatus) {
    this.digitStatus = digitStatus;
  }

  public Integer getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(Integer projectStatus) {
    this.projectStatus = projectStatus;
  }

  public Integer getAlarmStatus() {
    return alarmStatus;
  }

  public void setAlarmStatus(Integer alarmStatus) {
    this.alarmStatus = alarmStatus;
  }

  @Override
  public String toString() {
    return "AutoExamEntity{" +
        "subprojectId=" + subprojectId +
        ", prjname='" + prjname + '\'' +
        ", emuStatus=" + emuStatus +
        ", simStatus='" + simStatus + '\'' +
        ", simRemainRate='" + simRemainRate + '\'' +
        ", simEffectDate='" + simEffectDate + '\'' +
        ", digitStatus=" + digitStatus +
        ", projectStatus=" + projectStatus +
        ", alarmStatus=" + alarmStatus +
        '}';
  }
}