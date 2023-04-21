package com.shmet.vo.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author
 */
public class AppUserLoginReq {
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = "^1[0-9]{10}$", message = "手机号格式不合法,请输入正确的手机号")
  private String phone;
  @NotBlank(message = "密码不能为空")
  private String password;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
