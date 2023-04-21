package com.shmet.vo;

import com.shmet.entity.mysql.gen.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Getter
@Setter
public class CustomerListVo {
  private Integer customerId;
  private String customerNo;
  private String customerName;
  private String customerAdminCount;
  private String adminName;
  private String companyLogo;

  public static CustomerListVo build(Customer customer) {
    CustomerListVo vo = new CustomerListVo();
    vo.setCustomerId(customer.getCustomerId());
    vo.setCustomerNo(customer.getCustomerNo());
    vo.setCustomerName(customer.getCustomerName());
    vo.setCustomerAdminCount(customer.getAdminAccount());
    vo.setAdminName(customer.getContactName());
    vo.setCompanyLogo(customer.getLogo());
    return vo;
  }

  public static List<CustomerListVo> buildVos(List<Customer> customers) {
    return customers.stream().parallel().map(CustomerListVo::build).collect(toList());
  }
}
