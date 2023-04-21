package com.shmet.vo.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author
 */
public class CustomerModifyReq {
  @NotNull(message = "客户Id不能为空")
  private Integer customerId;
  @NotBlank(message = "客户姓名不能为空")
  private String customerName;
  @NotBlank(message = "管理员姓名不能为空")
  private String adminName;
  private String logoUrl;

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getAdminName() {
    return adminName;
  }

  public void setAdminName(String adminName) {
    this.adminName = adminName;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }
}
