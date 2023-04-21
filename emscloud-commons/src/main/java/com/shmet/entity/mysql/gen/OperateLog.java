package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TableName(value = "t_operate_log")
public class OperateLog {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String account;
  private String username;
  private String ip;
  private String time = getCurrentTime();
  //日志类型
  private Integer logType;
  //操作内容
  private String content;
  //传入参数
  private String param;
  //操作平台
  private String opPlatform = "none";

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getTime() {
    return time;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Integer getLogType() {
    return logType;
  }

  public void setLogType(Integer logType) {
    this.logType = logType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public String getOpPlatform() {
    return opPlatform;
  }

  public void setOpPlatform(String opPlatform) {
    this.opPlatform = opPlatform;
  }

  public static String getCurrentTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return formatter.format(LocalDateTime.now());
  }

  @Override
  public String toString() {
    return "OperateLog{" +
        "id=" + id +
        ", account='" + account + '\'' +
        ", username='" + username + '\'' +
        ", ip='" + ip + '\'' +
        ", time='" + time + '\'' +
        ", logType=" + logType +
        ", content='" + content + '\'' +
        ", param='" + param + '\'' +
        ", opPlatform='" + opPlatform + '\'' +
        '}';
  }
}
