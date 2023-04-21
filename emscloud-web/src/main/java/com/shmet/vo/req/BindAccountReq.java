package com.shmet.vo.req;

import javax.validation.constraints.NotBlank;

/**
 * @author
 */
public class BindAccountReq {
  @NotBlank(message = "绑定Id不能为空")
  private Long id;
  @NotBlank(message = "待绑定账户不能为空")
  private String account;
  @NotBlank(message = "待绑定账户密码不能为空")
  private String password;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
