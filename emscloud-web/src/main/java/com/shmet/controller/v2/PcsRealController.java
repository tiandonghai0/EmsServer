package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.PcsRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class PcsRealController {

  @Autowired
  private PcsRealService pcsRealService;

  @GetMapping("/pcs/basicinfo")
  @UserLoginToken
  public Result listPcsBasicInfo(@RequestParam Long subprojectid) {
    return Result.getSuccessResultInfo(pcsRealService.getPcsRealDataFromRedis(subprojectid));
  }
}
