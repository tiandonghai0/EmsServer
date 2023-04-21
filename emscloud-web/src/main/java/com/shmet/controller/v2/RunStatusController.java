package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.RunStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class RunStatusController {

  @Autowired
  private RunStatusService runStatusService;

  @GetMapping("/runstatus")
  @UserLoginToken
  public Result runStatusApi(@RequestParam String customerNo) {
    return Result.getSuccessResultInfo(runStatusService.getFromRedis(customerNo));
  }

  @PostMapping("/runstatusV2")
  @UserLoginToken
  public Result runstatusApiV2(@RequestBody JSONObject jsonObject) {
    try {
      return Result.getSuccessResultInfo(runStatusService.getFromRedis2(jsonObject.getLong("subProjectId")));
    } catch (Exception ex) {
      return Result.getErrorResultInfo(ex.getMessage());
    }
  }

  @PostMapping("/runstatusInfoV2")
  @UserLoginToken
  public Result runstatusInfoV2(@RequestBody JSONObject jsonObject){
    return Result.getSuccessResultInfo(runStatusService.getStatusV2(jsonObject.getLong("subProjectId")));
  }
}
