package com.shmet.vo.req;

import com.shmet.entity.mysql.gen.User;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author
 */
public class UserRegisterReq {
  @NotBlank(message = "账户名不能为空")
  private String account;
  @NotBlank(message = "密码不能为空")
  private String password;
  @NotBlank(message = "手机验证码不能为空")
  private String phoneCode;
  private String projectNo;

  public User buildUser() {
    User user = new User();
    user.setAccount(this.getAccount());
    user.setPassword(this.getPassword());
    user.setEmail("1");
    user.setName("anonymous");
    user.setProjectNo(this.getProjectNo());
    user.setCreateDate(new Date());
    user.setUpdateDate(new Date());
    return user;
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

  public String getProjectNo() {
    return projectNo;
  }

  public void setProjectNo(String projectNo) {
    this.projectNo = projectNo;
  }

  public String getPhoneCode() {
    return phoneCode;
  }

  public void setPhoneCode(String phoneCode) {
    this.phoneCode = phoneCode;
  }
}
