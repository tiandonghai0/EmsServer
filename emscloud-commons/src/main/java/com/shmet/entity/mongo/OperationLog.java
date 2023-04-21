package com.shmet.entity.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "operationLog")
public class OperationLog {
  private String account;
  private String username;
  //1:登录 2:新增 3:修改 4:删除 5:查询 6:下发指令
  private int logType;
  //操作内容
  private String content;
  //客户端Ip
  private String clientIp;
  //操作时间
  private Long operateTime = getCurrentTime();
  //操作平台
  private String operatePlatform;

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getLogType() {
    return logType;
  }

  public void setLogType(int logType) {
    this.logType = logType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  public String getOperatePlatform() {
    return operatePlatform;
  }

  public void setOperatePlatform(String operatePlatform) {
    this.operatePlatform = operatePlatform;
  }

  public static Long getCurrentTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    return Long.parseLong(
        formatter.format(LocalDateTime.now())
    );
  }
}