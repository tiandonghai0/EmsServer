package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.handler.v2.CustomerInfoService;
import com.shmet.vo.CustomerInfoVo;
import com.shmet.vo.req.CustomerModifyReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 * 客户基础信息Restful API
 */
@RestController
@RequestMapping("/v2/customerinfo")
public class CustomerInfoController {

  private static final Logger logger = LoggerFactory.getLogger(CustomerInfoController.class);

  @Autowired
  private CustomerInfoService customerInfoService;

  //@OperationLogAnnotation(logType = 5, opContent = "查询客户基础信息")
  @UserLoginToken
  @GetMapping
  public Result getBasicInfo(@RequestParam String customerNo) {
    CustomerInfoVo infoVo = customerInfoService.queryBasicInfo(customerNo);
    return Result.getSuccessResultInfo(infoVo);
  }

  @UserLoginToken
  @PostMapping("/pagelist")
  public ResultSearch getBasicInfo(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String customerName) {
    return customerInfoService.customerPageList(pageNum, pageSize, customerName);
  }

  @UserLoginToken
  @PostMapping("/uploadlogo")
  public Result uploadLogo(@RequestParam String module, @RequestParam MultipartFile file) {
    return Result.getSuccessResultInfo(customerInfoService.uploadLogo(module, file));
  }

  @UserLoginToken
  @PostMapping("/update")
  public Result updateCustomerInfo(@Validated @RequestBody CustomerModifyReq req) {
    return Result.getSuccessResultInfo(customerInfoService.modifyCustomerInfo(req));
  }

  @GetMapping("/hotload/customerlist")
  public Result hotLoadCustomerList(@RequestParam String cname) {
    return Result.getSuccessResultInfo(customerInfoService.getCustomerListVos(cname));
  }

  @PostMapping("/getemslist")
  public Result getEmsList(@RequestBody JSONObject jsonObject) {
    return Result.getSuccessResultInfo(customerInfoService.getCustomerByVersion(jsonObject.getString("version")));
  }
}
