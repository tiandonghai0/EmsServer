package com.shmet.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 返回分页数据
 */
public class ResultSearch implements Serializable {

  public ResultSearch() {

  }

  public ResultSearch(int pageNo, int pageSize) {
    this.pageSize = pageSize;
    this.pageNo = pageNo;
  }

  public ResultSearch(int pageNo, int pageSize, long totalCount) {
    this.pageSize = pageSize;
    this.pageNo = pageNo;
    this.totalCount = totalCount;
  }

  public ResultSearch(ResultCode code, Boolean status, Object data, String message) {
    super();
    this.code = code.toString();
    this.success = status;
    this.data = data;
    this.message = message;
  }

  private String code;
  private String message;
  private Boolean success;

  /*
   * 当前页
   */
  public Integer pageNo = 1;

  /*
   * 每页显示多少条
   */
  public Integer pageSize = 10;

  /*
   * 总共有多少页
   */
  public long pageCount = 0;

  /*
   * 总数
   */
  public long totalCount = 0;


  /**
   * 当前页中存放的数据
   */
  private Object data;


  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public long getPageCount() {
    long pc = totalCount / pageSize;
    return totalCount % pageSize == 0 ? pc : pc + 1;
  }

  public void setPageCount(long pageCount) {
    this.pageCount = pageCount;
  }

  public long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
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


  /**
   * 描述：产生一个成功不带参数的结果
   */
  public static ResultSearch getSuccessResultInfo() {
    return new ResultSearch(ResultCode.SUCCESS, true, null, null);
  }

  /**
   * 描述：产生一个成功带参数的结果
   */
  public static ResultSearch getSuccessResultInfo(List<?> data, String message) {
    return new ResultSearch(ResultCode.SUCCESS, true, data, message);
  }


  /**
   * 描述：产生一个成功带参数的结果
   */
  public static ResultSearch getSuccessResultInfo(String message) {
    return new ResultSearch(ResultCode.SUCCESS, true, null, message);
  }

  /**
   * 描述：产生一个成功带参数的结果
   */
  public static ResultSearch getSuccessResultInfo(List<?> data) {
    return new ResultSearch(ResultCode.SUCCESS, true, data, null);
  }


  /**
   * 描述：产生一个失败不带参数的结果
   */
  public static ResultSearch getErrorResultInfo() {
    return new ResultSearch(ResultCode.ERROR, false, null, null);
  }

  /**
   * 描述：产生一个失败带参数的结果
   */
  public static ResultSearch getErrorResultInfo(List<?> data, String message) {
    return new ResultSearch(ResultCode.ERROR, false, data, message);
  }

  /**
   * 描述：产生一个失败带参数的结果
   */
  public static ResultSearch getErrorResultInfo(String message) {
    return new ResultSearch(ResultCode.ERROR, false, null, message);
  }

  /**
   * 描述：产生一个失败带参数的结果
   */
  public static ResultSearch getErrorResultInfo(List<?> data) {
    return new ResultSearch(ResultCode.ERROR, false, data, null);
  }

}

