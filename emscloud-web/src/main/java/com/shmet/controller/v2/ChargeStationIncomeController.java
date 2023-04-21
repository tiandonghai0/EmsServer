package com.shmet.controller.v2;

import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.ChargeStationIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author
 * 充电桩收益 相关Restful接口
 */
@RestController
@RequestMapping("/v2/chargestation")
public class ChargeStationIncomeController {

  @Autowired
  private ChargeStationIncomeService chargeStationIncomeService;

  @GetMapping("/down/datas")
  public Result calcEchartDatas(@RequestParam(required = false) String date,
                                @RequestParam List<Long> subprojectIds,
                                @RequestParam(required = false, defaultValue = "1") int dateType,
                                @RequestParam(required = false, defaultValue = "4") int deviceModelType) {
    return Result.getSuccessResultInfo(chargeStationIncomeService.calcEchartDatas(date, subprojectIds, dateType, deviceModelType));
  }
}
