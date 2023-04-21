package com.shmet.vo.req;

import com.shmet.entity.mysql.gen.AppUser;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author
 */
public class AppUserRegisterReq {
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = "^1[0-9]{10}$", message = "手机号格式不合法,请输入正确的手机号")
  private String phone;
  @NotBlank(message = "手机验证码不能为空")
  private String phoneCode;
  @NotBlank(message = "密码不能为空")
  private String password;

  public AppUser buildAppUser() {
    AppUser appUser = new AppUser();
    appUser.setPhone(this.getPhone());
    appUser.setPassword(DigestUtils.md5Hex(this.getPassword()));
    return appUser;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhoneCode() {
    return phoneCode;
  }

  public void setPhoneCode(String phoneCode) {
    this.phoneCode = phoneCode;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
