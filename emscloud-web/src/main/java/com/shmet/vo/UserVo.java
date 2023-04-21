package com.shmet.vo;

/**
 * @author
 */
public class UserVo {
  private String account;
  private String password;

  public void setAccount(String account) {
    this.account = account;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAccount() {
    return account;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "UserVo{" +
        "account='" + account +
        '}';
  }
}
