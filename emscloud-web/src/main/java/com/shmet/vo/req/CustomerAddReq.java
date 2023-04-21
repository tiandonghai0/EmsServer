package com.shmet.vo.req;

import com.shmet.entity.mysql.gen.Customer;
import com.shmet.entity.mysql.gen.User;
import com.shmet.entity.mysql.gen.UserRoleRel;
import com.shmet.thirdpart.EmsUuid;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author
 */
@Getter
@Setter
public class CustomerAddReq {
  @NotBlank(message = "客户编码不能为空")
  private String customerNo;
  @NotBlank(message = "客户名称不能为空")
  private String customerName;
  @NotBlank(message = "管理员姓名不能为空")
  private String adminName;
  @NotBlank(message = "管理员账号不能为空")
  private String adminAccount;
  @NotBlank(message = "密码不能为空")
  private String pwd;
  @NotBlank(message = "确认密码不能为空")
  private String cfmPwd;
  private String logoUrl;

  public Customer buildCustomer() {
    Customer customer = new Customer();
    customer.setCustomerNo(this.getCustomerNo());
    customer.setCustomerName(this.getCustomerName());
    customer.setAdminAccount(this.getAdminAccount());
    return customer;
  }

  public User buildUser() {
    User user = new User();
    user.setProjectNo(this.getCustomerNo());
    user.setName(this.getAdminName());
    user.setAccount(this.getAdminAccount());
    user.setPassword(this.getPwd());
    user.setCreateDate(new Date());
    user.setUpdateDate(new Date());
    return user;
  }

  public UserRoleRel buildUserRoleRel() {
    UserRoleRel userRoleRel = new UserRoleRel();
    userRoleRel.setId(EmsUuid.getInstance().getUUId());
    userRoleRel.setUserId(this.adminAccount);
    return userRoleRel;
  }
}
