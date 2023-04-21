package com.shmet.bean;

import com.alibaba.fastjson.JSONObject;

public class EHang365StartStopResBean {
	private Integer code;
	
	private String msg;

	private JSONObject data;

  /**
   * @return the code
   */
  public Integer getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode(Integer code) {
    this.code = code;
  }

  /**
   * @return the msg
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param msg the msg to set
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * @return the data
   */
  public JSONObject getData() {
    return data;
  }

  /**
   * @param data the data to set
   */
  public void setData(JSONObject data) {
    this.data = data;
  }

}