package com.shmet.vo;

/**
 * @author
 */
public class UpdatePwdVo {
  private String projectNo;
  private String account;
  private String oldSecondPwd;
  private String newSecondPwd;

  public String getProjectNo() {
    return projectNo;
  }

  public void setProjectNo(String projectNo) {
    this.projectNo = projectNo;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getOldSecondPwd() {
    return oldSecondPwd;
  }

  public void setOldSecondPwd(String oldSecondPwd) {
    this.oldSecondPwd = oldSecondPwd;
  }

  public String getNewSecondPwd() {
    return newSecondPwd;
  }

  public void setNewSecondPwd(String newSecondPwd) {
    this.newSecondPwd = newSecondPwd;
  }
}
