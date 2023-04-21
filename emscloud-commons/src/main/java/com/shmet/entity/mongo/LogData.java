package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logData")
@CompoundIndexes({  
  @CompoundIndex(name = "logCatagory_timestamp_idx", def = "{'logCatagory':1, 'timestamp': -1}")  
})
public class LogData {
  @Id
  private String id;
  
  @NotNull
  private int logCatagory;
  
  @NotNull
  private int logSubCatagory;

  @NotNull 
  private String logLevel;
  
  @NotNull 
  private Long timestamp;
  
  @NotNull 
  private Integer projectId;
  
  @NotNull 
  private Long subProjectId;
  
  @NotNull 
  private Long acconeId;
  
  @NotNull
  private String message;
  
  @NotNull
  private String userName;
  
  static public String getCollectionName () {
    return "logData";
  }

  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }


  public int getLogCatagory() {
    return logCatagory;
  }

  public void setLogCatagory(int logCatagory) {
    this.logCatagory = logCatagory;
  }


  public int getLogSubCatagory() {
    return logSubCatagory;
  }


  public void setLogSubCatagory(int logSubCatagory) {
    this.logSubCatagory = logSubCatagory;
  }


  public String getLogLevel() {
    return logLevel;
  }


  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }


  public Long getTimestamp() {
    return timestamp;
  }


  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }


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


  public Long getAcconeId() {
    return acconeId;
  }


  public void setAcconeId(Long acconeId) {
    this.acconeId = acconeId;
  }


  public String getMessage() {
    return message;
  }


  public void setMessage(String message) {
    this.message = message;
  }


  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

}
