package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.AutoExamineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author
 */
@RestController
public class AutoExamController {

  @Autowired
  private AutoExamineService autoExamineService;

  //@OperationLogAnnotation
  @PostMapping("/autoexam/query/{pageNum:\\d+}/{pageSize:\\d+}")
  public AutoExamineService.AutoExamResult query(@PathVariable Integer pageNum,
                                                 @PathVariable Integer pageSize,
                                                 @RequestParam Map<String, String> map) throws ExecutionException, InterruptedException {
    return autoExamineService.query(pageNum, pageSize, map).get();
  }

  //@OperationLogAnnotation
  @GetMapping("/autoexam/active")
  public Result autoExam() {
    autoExamineService.examine();
    return Result.getSuccessResultInfo("巡检成功");
  }

  @PostMapping("/getSimInfo")
  public JSONObject getNewSimInfo(@RequestBody(required = false) AutoExamineService.IccIdReq req) {
    return autoExamineService.getNewSimInfo(req);
  }
}
