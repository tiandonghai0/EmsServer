package com.shmet.controller.v2;

import com.shmet.entity.dto.ResultSearch;
import com.shmet.handler.v2.OperationLogService;
import com.shmet.vo.req.OpLogConditionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 */
@RestController
public class OperationLogController {

  @Autowired
  private OperationLogService operationLogService;

  @PostMapping("/oplog/pagequery")
  public ResultSearch PageQuery(@RequestBody OpLogConditionQuery query) {
    return operationLogService.PageQuery(query);
  }

  @PostMapping("/oplog/mysql/query")
  public ResultSearch pageQueryMySQL(@RequestBody OpLogConditionQuery query) {
    return operationLogService.pageQueryMySQL(query);
  }
}
