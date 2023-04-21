package com.shmet.controller.v2;

import com.google.common.collect.Lists;
import com.shmet.ArithUtil;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.CommonService;
import com.shmet.handler.v2.FineGritIncomeService;
import com.shmet.handler.v2.IncomeService;
import com.shmet.handler.v2.StoreEnergyIncomeService;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class IncomeController {

  @Autowired
  private FineGritIncomeService fineGritIncomeService;

  @Autowired
  private IncomeService incomeService;

  @Autowired
  private StoreEnergyIncomeService storeEnergyIncomeService;

  @Autowired
  private CommonService commonService;

  @GetMapping("/tt11")
  public Result t1(@RequestParam(required = false) String date, @RequestParam List<Long> subprojectIds) {
    return Result.getSuccessResultInfo(commonService.getCurrentDayIncome(date, Lists.newArrayList(subprojectIds)));
  }

  //计算上方的 发电量 以及 总收益 数据 读取实时数据
  @GetMapping("/calc/top/epe-income")
  @UserLoginToken
  public Result calcEpeAndIncome(@RequestParam(required = false) String date,
                                 @RequestParam(required = false, defaultValue = "1") int dateType,
                                 @RequestParam List<Long> subprojectIds,
                                 @RequestParam(required = false, defaultValue = "2") int deviceModelType) {
    Map<String, Double> currentMap = storeEnergyIncomeService.calcEpeAndIncome(date, dateType, subprojectIds, deviceModelType);
    Map<String, Double> beforeMap = storeEnergyIncomeService.calcEpeAndIncome(getBeforeDayOrMonthOrYear(date, dateType), dateType, subprojectIds, deviceModelType);

    double currentepesum = currentMap.get("epesum") == null ? 0d : currentMap.get("epesum");
    double currentepeprice = currentMap.get("epeprice") == null ? 0d : currentMap.get("epeprice");
    double beforeepesum = beforeMap.get("epesum") == null ? 0d : beforeMap.get("epesum");
    double beforeepeprice = beforeMap.get("epeprice") == null ? 0d : beforeMap.get("epeprice");

    currentMap.put("epesumsubval", NumberUtil.convert2(ArithUtil.sub(currentepesum, beforeepesum)));
    currentMap.put("epepricesubval", NumberUtil.convert2(ArithUtil.sub(currentepeprice, beforeepeprice)));

    return Result.getSuccessResultInfo(currentMap);
  }

  private String getBeforeDayOrMonthOrYear(String date, int dateType) {
    //获取前一天
    if (dateType == 1) {
      if (StringUtils.isNotBlank(date)) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter).minusDays(1).toString();
      } else {
        return LocalDate.now().minusDays(1).toString();
      }
    }

    //获取前一月
    if (dateType == 2) {
      if (StringUtils.isNotBlank(date)) {
        //转换为 天的格式
        date = date + "-11";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sdate = LocalDate.parse(date, formatter).minusMonths(1).toString();
        return StringUtils.substringBeforeLast(sdate, "-");
      } else {
        return StringUtils.substringBeforeLast(LocalDate.now().minusMonths(1).toString(), "-");
      }
    }

    //获取前一年
    if (dateType == 3) {
      if (StringUtils.isNotBlank(date)) {
        return String.valueOf(Long.parseLong(date) - 1);
      } else {
        return String.valueOf(Year.now().getValue() - 1);
      }
    }

    return "-1";
  }

  /**
   * @param date       日期 格式必须是yyyy-MM-dd
   * @param customerId 客户id eg. 国开的客户id是 8
   * @return Double Data
   */
  @GetMapping("/income/analyze")
  @UserLoginToken
  public Result testDayIncome(@RequestParam String date, @RequestParam Integer customerId) {
    List<Long> subprojectIds = incomeService.getSubProjectIds(customerId);
    //Map<String, Object> currentDayIncomeMap = commonService.getCurrentDayIncome(date, subprojectIds);
    //Double income = currentDayIncomeMap.get("income") == null ? 0d : (Double) currentDayIncomeMap.get("income");

    //日收益
    Double dayIncome= fineGritIncomeService.calcNavigateValV2(date, subprojectIds.get(0), 1);

    //月收益
    Double monthIncome= fineGritIncomeService.calcNavigateValV2(date.substring(0,7), subprojectIds.get(0), 2);

    //年收益
    Double yearIncome= fineGritIncomeService.calcNavigateValV2(date.substring(0,4), subprojectIds.get(0), 3);

//    Double dayIncome = incomeService.dayIncome(date, customerId);
//    Double weekIncome = incomeService.weekIncome(date, customerId);
//    Double monthIncome = incomeService.monthIncome(date.substring(0, 7), customerId);
//    Double yearIncome = incomeService.yearIncome(date.substring(0, 4), customerId);
    Double totalIncome = incomeService.totalIncome(customerId);

    Map<String, Double> map = new LinkedHashMap<>();
    map.put("dayIncome", dayIncome );
    map.put("weekIncome",0.0d);
    map.put("monthIncome", monthIncome );
    map.put("yearIncome", yearIncome );
    map.put("totalIncome", totalIncome );

    return Result.getSuccessResultInfo(map);
  }

  @GetMapping("/day/epeandepi")
  @UserLoginToken
  public Result getEpeAndEpiByDay(@RequestParam(required = false) String date,
                                  @RequestParam List<Long> subprojectIds) {
    return Result.getSuccessResultInfo(storeEnergyIncomeService.getEpeAndEpiByDay(date, subprojectIds));
  }

  @GetMapping("/month/epeandepi")
  @UserLoginToken
  public Result getEpeAndEpiByMonth(@RequestParam(required = false) String date,
                                    @RequestParam List<Long> subprojectIds) {
    if (StringUtils.isBlank(date)) {
      date = Year.now().getValue() + "-" + YearMonth.now().getMonthValue();
    }
    return Result.getSuccessResultInfo(storeEnergyIncomeService.getEpeAndEpiByMonth(date, subprojectIds));
  }

  @GetMapping("/year/epeandepi")
  @UserLoginToken
  public Result getEpeAndEpiByYear(@RequestParam(required = false) String date,
                                   @RequestParam List<Long> subprojectIds) {
    if (StringUtils.isBlank(date)) {
      date = String.valueOf(Year.now().getValue());
    }
    return Result.getSuccessResultInfo(storeEnergyIncomeService.getEpeAndEpiByYear(date, subprojectIds));
  }

  @GetMapping("/group/data")
  @UserLoginToken
  public Result groupRealData(@RequestParam(required = false) String date,
                              @RequestParam Long subprojectIds,
                              @RequestParam int flag) {
    return Result.getSuccessResultInfo(storeEnergyIncomeService.groupRealData(date, subprojectIds, flag));
  }

  @GetMapping("/calc/subval")
  @UserLoginToken
  public Result calcElecSubVal(@RequestParam(required = false) String date,
                               @RequestParam Long subprojectIds,
                               @RequestParam int flag) {
    if (StringUtils.isBlank(date)) {
      date = LocalDate.now().toString();
    }
    return Result.getSuccessResultInfo(storeEnergyIncomeService.calcElecSubVal(date, subprojectIds, flag));
  }

  @GetMapping("/calc/month")
  @UserLoginToken
  public Result calcMonth(@RequestParam String month, @RequestParam List<Long> subprojectIds, @RequestParam(required = false, defaultValue = "2") int flag) {
    return Result.getSuccessResultInfo(storeEnergyIncomeService.calcMonth(month, subprojectIds, flag));
  }

  //光电 按年统计
  @GetMapping("/calc/year")
  @UserLoginToken
  public Result calcYear(@RequestParam String year, @RequestParam List<Long> subprojectIds, @RequestParam(required = false, defaultValue = "2") int flag) {
    return Result.getSuccessResultInfo(storeEnergyIncomeService.calcYear(year, subprojectIds, flag));
  }

  @GetMapping("/calc/day")
  @UserLoginToken
  public Result calcDay(@RequestParam(required = false) String date, @RequestParam List<Long> subprojectIds, @RequestParam(required = false, defaultValue = "2") int flag) {
    return Result.getSuccessResultInfo(storeEnergyIncomeService.calcDay(date, subprojectIds, flag));
  }

}
