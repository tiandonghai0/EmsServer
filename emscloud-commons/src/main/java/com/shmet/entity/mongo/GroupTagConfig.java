package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 设备分组配置信息
 *
 */
@Document(collection = "groupTagConfig")
public class GroupTagConfig {
  @Id
  private String id;
  
  @NotNull
  private Long groupNo;
  
  @NotNull
  private String groupName;
  
  @NotNull 
  private Integer projectId;
  
  @NotNull 
  private Long subProjectId;

  @NotNull
  private List<String> statisticsMethods;

  @NotNull
  private String realRecordMethod;
  
  @NotNull
  private List<GroupDeviceTagItem> deviceNoTagCodeList;
  

  public Integer getProjectId() {
    return projectId;
  }


  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public Long getSubProjectId() {
    return subProjectId;
  }


  public void setSubProjectId(Long subProjectId) {
    this.subProjectId = subProjectId;
  }

  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }

  public Long getGroupNo() {
    return groupNo;
  }


  public void setGroupNo(Long groupNo) {
    this.groupNo = groupNo;
  }


  public String getGroupName() {
    return groupName;
  }


  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }


  public List<GroupDeviceTagItem> getDeviceNoTagCodeList() {
    return deviceNoTagCodeList;
  }


  public void setDeviceNoTagCodeList(List<GroupDeviceTagItem> deviceNoTagCodeList) {
    this.deviceNoTagCodeList = deviceNoTagCodeList;
  }


  public List<String> getStatisticsMethods() {
    return statisticsMethods;
  }


  public void setStatisticsMethods(List<String> statisticsMethods) {
    this.statisticsMethods = statisticsMethods;
  }


  public String getRealRecordMethod() {
    return realRecordMethod;
  }


  public void setRealRecordMethod(String realRecordMethod) {
    this.realRecordMethod = realRecordMethod;
  }

}
