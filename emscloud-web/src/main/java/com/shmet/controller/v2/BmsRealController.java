package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.BmsRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author
 */
@RestController
@RequestMapping("/v2/bms")
public class BmsRealController {

  @Autowired
  private BmsRealService bmsRealService;

  @GetMapping("/realdata")
  @UserLoginToken
  public Result getBmsRealData(@RequestParam Long subprojectid) {
    return Result.getSuccessResultInfo(bmsRealService.getBmsDataFromRedis(subprojectid));
  }

  @PostMapping("/AllElectricityCount")
  @UserLoginToken
  public Result getAllElectricityCount(@RequestBody JSONObject param){
    
    return  Result.getSuccessResultInfo();
  }
}
