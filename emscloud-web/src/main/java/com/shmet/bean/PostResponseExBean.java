package com.shmet.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author
 */
public class PostResponseExBean extends PostResponseBean {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String token;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Object datas;

  public Boolean projectAdmin;

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public Object getDatas() {
    return datas;
  }


  public void setDatas(Object datas) {
    this.datas = datas;
  }

  public Boolean getProjectAdmin() {
    return projectAdmin;
  }

  public void setProjectAdmin(Boolean projectAdmin) {
    this.projectAdmin = projectAdmin;
  }
}
