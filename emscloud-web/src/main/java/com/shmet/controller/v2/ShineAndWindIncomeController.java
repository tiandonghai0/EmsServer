package com.shmet.controller.v2;

import com.shmet.ArithUtil;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.ShineAndWindIncomeService;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author
 */
@RestController
@RequestMapping("/v2/shine-wind")
public class ShineAndWindIncomeController {

  @Autowired
  private ShineAndWindIncomeService shineAndWindIncomeService;

  //默认按月 查询光电
  @GetMapping("/month-year/time-period/data")
  @UserLoginToken
  public Result calcMonthOrYearTimePeriodDatas(@RequestParam(required = false) String date,
                                               @RequestParam Long subprojectId,
                                               @RequestParam(required = false, defaultValue = "1") int dateType,
                                               @RequestParam(required = false, defaultValue = "2") int deviceModelType) {
    Map<String, Double> currentMap = shineAndWindIncomeService.calcMonthOrYearTimePeriodDatas(date, subprojectId, dateType, deviceModelType);
    String beforeMonthOrYear = getBeforeMonthOrYear(date, dateType);
    Map<String, Double> beforeMap = shineAndWindIncomeService.calcMonthOrYearTimePeriodDatas(StringUtils.substringBeforeLast(beforeMonthOrYear, "-"), subprojectId, dateType, deviceModelType);
    Double cfeng = getaDouble(currentMap, "1");
    Double cping = getaDouble(currentMap, "3");
    Double cgu = getaDouble(currentMap, "2");
    //Double cjfeng = (currentMap.get("4") == null ? 0d : currentMap.get("4"));

    Double bfeng = getaDouble(beforeMap, "1");
    Double bping = getaDouble(beforeMap, "3");
    Double bgu = getaDouble(beforeMap, "2");
    //Double bjfeng = (beforeMap.get("4") == null ? 0d : beforeMap.get("4"));

    currentMap.put("fengSubVal", NumberUtil.convert2(ArithUtil.sub(cfeng, bfeng)));
    currentMap.put("pingSubVal", NumberUtil.convert2(ArithUtil.sub(cping, bping)));
    currentMap.put("guSubVal", NumberUtil.convert2(ArithUtil.sub(cgu, bgu)));
    return Result.getSuccessResultInfo(currentMap);
  }

  private Double getaDouble(Map<String, Double> currentMap, String s) {
    if (currentMap.get(s) == null) {
      currentMap.put(s, 0d);
      return 0d;
    }
    return currentMap.get(s);
  }

  private String getBeforeMonthOrYear(String date, int dateType) {
    if (dateType == 1) {
      if (StringUtils.isBlank(date)) {
        String tmp = LocalDate.now().minusMonths(1).toString();
        return StringUtils.replace(StringUtils.substringBeforeLast(tmp, "-"), "-", "");
      } else {
        String tmp = date + "-06";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(tmp, formatter).minusMonths(1).toString();
      }
    }

    if (dateType == 2) {
      if (StringUtils.isBlank(date)) {
        String sdate = LocalDate.now().minusYears(1).toString();
        return StringUtils.substringBefore(sdate, "-");
      } else {
        return String.valueOf(Long.parseLong(date) - 1);
      }
    }

    return "";
  }
}
