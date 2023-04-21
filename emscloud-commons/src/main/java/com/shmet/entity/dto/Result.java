package com.shmet.entity.dto;

import java.io.Serializable;

/**
 * 返回标准数据
 */
public class Result implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * 状态编码
   */
  private String code = "0";

  /**
   * 返回消息
   */
  private String message;

  /**
   * 成功与失败
   */
  private Boolean success = false;

  /**
   * 返回数据
   */
  private Object data;

  //还需要写一个空的构造方法
  public Result() {
    super();
  }

  public Result(String code, Boolean status, Object data, String message) {
    super();
    this.code = code;
    this.success = status;
    this.data = data;
    this.message = message;
  }

  public Result(ResultCode code, Boolean status, Object data, String message) {
    super();
    this.code = code.toString();
    this.success = status;
    this.data = data;
    this.message = message;
  }

  public Result(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public Result(ResultCode error, boolean b, String message, Object data) {
    this.code = error.toString();
    this.success = b;
    this.message = message;
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  /**
   * 描述：产生一个成功不带参数的结果
   */
  public static Result getSuccessResultInfo() {
    return new Result(ResultCode.SUCCESS, true, null, null);
  }

  /**
   * 描述：产生一个成功带参数的结果
   */
  public static Result getSuccessResultInfo(Object data, String message) {
    return new Result(ResultCode.SUCCESS, true, data, message);
  }

  /**
   * 描述：产生一个成功带参数的结果
   */
  public static Result getSuccessResultInfo(String code, Object data, String message) {
    return new Result(code, true, data, message);
  }


  /**
   * 描述：产生一个成功带参数的结果
   */
  public static Result getSuccessResultInfo(String message) {
    return new Result(ResultCode.SUCCESS, true, message, null);
  }

  /**
   * 描述：产生一个成功带参数的结果
   */
  public static Result getSuccessResultInfo(Object data) {
    return new Result(ResultCode.SUCCESS, true, data, null);
  }

  /**
   * 描述：产生一个失败不带参数的结果
   */
  public static Result getErrorResultInfo(Integer code, String message) {
    return new Result(String.valueOf(code), message);
  }

  public static Result getErrorResultInfo(String message, Object data) {
    return new Result(ResultCode.ERROR, false, message, data);
  }

  /**
   * 描述：产生一个失败带参数的结果
   */
  public static Result getErrorResultInfo(String message) {
    return new Result(ResultCode.ERROR, false, message, null);
  }
}
