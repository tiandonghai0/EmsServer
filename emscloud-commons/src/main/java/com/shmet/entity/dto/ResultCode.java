package com.shmet.entity.dto;

public enum ResultCode {
  //枚举常量定义的同时指定状态码
  NOT_LOGIN(-1),
  ERROR(0),
  SUCCESS(1),
  NOT_TOKEN(4002),
  USER_NOT_EXIST(401),
  TOKEN_EXPIRED(4001),
  PARAM_ERROR(4003),
  USER_ALREADY_EXIST(4005),
  NOT_AUTH(2);

  private final int code; //状态码值

  ResultCode(int code) { //非public构造方法
    this.code = code;
  }

  @Override
  public String toString() {
    return String.valueOf(code);
  }
}