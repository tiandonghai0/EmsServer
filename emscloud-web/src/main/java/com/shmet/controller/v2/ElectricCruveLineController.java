package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mysql.gen.ChargeDataByMonth;
import com.shmet.entity.mysql.gen.ChargeDataByYear;
import com.shmet.handler.v2.ElectricCruveLineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class ElectricCruveLineController {

  @Autowired
  private ElectricCruveLineService eservice;

  @GetMapping("/select/by/month")
  @UserLoginToken
  public Result selectDataByMonth(@RequestParam String ymonth, @RequestParam Long subProejctId) {
    eservice.saveByDataToDb(ymonth, subProejctId, 1);
    List<ChargeDataByMonth> list = eservice.findDayList(ymonth, subProejctId);
    boolean isSummerMonth = eservice.isSummerMonth(subProejctId, Integer.parseInt(StringUtils.split(ymonth, "/")[1]));
    //日期统计
    List<Integer> date = list.stream().map(o -> Integer.parseInt(StringUtils.substringAfterLast(o.getDate(), "-"))).collect(Collectors.toList());
    //峰时段
    List<Double> fengVals = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getFengVal).collect(Collectors.toList());
    //平时段
    List<Double> pingVals = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getPingVal).collect(Collectors.toList());
    //谷时段
    List<Double> guVals = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getGuVal).collect(Collectors.toList());
    //尖峰时段
    List<Double> jfengVals = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getJfengVal).collect(Collectors.toList());
    //当天峰时段电费
    List<Double> fengPeriodPrice = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getFengPeriodPrice).collect(Collectors.toList());
    //当天平时段电费
    List<Double> pingPeriodPrice = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getPingPeriodPrice).collect(Collectors.toList());
    //当天谷时段电费
    List<Double> guPeriodPrice = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getGuPeriodPrice).collect(Collectors.toList());
    //当天尖峰时段电费
    List<Double> jfengPeriodPrice = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getJfengPeriodPrice).collect(Collectors.toList());
    //当天各时段总用电量
    List<Double> daySums = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getDaySum).collect(Collectors.toList());
    //当月总用电量
    double totalSumElectricVal = daySums.stream().mapToDouble(o -> o).sum();
    //当天各时段总电费
    List<Double> daySumPrices = list.stream().filter(Objects::nonNull).map(ChargeDataByMonth::getDayElectricPrice).collect(Collectors.toList());
    //当月总电价
    double totalSumElectricPrice = daySumPrices.stream().mapToDouble(o -> o).sum();
    //一天中最大用电量
    Double maxDS = list.stream().map(ChargeDataByMonth::getDaySum).collect(Collectors.toList()).stream().max(Double::compareTo).orElse(0d);
    Double maxDaySum = convertPrecision(maxDS);
    //获取一个月中最大用电量是哪一天
    String d = list.stream().filter(o -> maxDaySum.equals(o.getDaySum())).findFirst().orElse(new ChargeDataByMonth()).getDate();
    String maxDay = (d == null ? "" : d.replace("-", "/"));
    Map<String, Object> map = new LinkedHashMap<>();

    map.put("date", date);
    map.put("fengVals", fengVals);
    map.put("pingVals", pingVals);
    map.put("guVals", guVals);
    if (isSummerMonth) {
      map.put("jfengVals", jfengVals);
      map.put("jfengPeriodPrice", jfengPeriodPrice);
    }
    map.put("fengPeriodPrice", fengPeriodPrice);
    map.put("pingPeriodPrice", pingPeriodPrice);
    map.put("guPeriodPrice", guPeriodPrice);
    map.put("daySums", daySums);
    map.put("daySumPrices", daySumPrices);
    map.put("totalSumElectricVal", convertPrecision(totalSumElectricVal));
    map.put("totalSumElectricPrice", convertPrecision(totalSumElectricPrice));
    map.put("maxDaySum", maxDaySum);
    map.put("maxDay", maxDay);

    return Result.getSuccessResultInfo(map);
  }

  @GetMapping("/select/by/year")
  @UserLoginToken
  public Result selectDataByYear(@RequestParam String year, @RequestParam Long subProejctId) {
    eservice.saveByDataToDb(year, subProejctId, 2);
    List<ChargeDataByYear> list = eservice.findMonthList(year, subProejctId);
    List<Integer> date = list.stream().map(o -> Integer.parseInt(StringUtils.substringAfterLast(o.getMonth(), "-"))).collect(Collectors.toList());
    List<Double> fengVals = getCollect(list, ChargeDataByYear::getFengVal);
    List<Double> pingVals = getCollect(list, ChargeDataByYear::getPingVal);
    List<Double> guVals = getCollect(list, ChargeDataByYear::getGuVal);
    List<Double> jfengVals = getCollect(list, ChargeDataByYear::getJfengVal);
    List<Double> fengPeriodPrice = getCollect(list, ChargeDataByYear::getFengPeriodPrice);
    List<Double> pingPeriodPrice = getCollect(list, ChargeDataByYear::getPingPeriodPrice);
    List<Double> guPeriodPrice = getCollect(list, ChargeDataByYear::getGuPeriodPrice);
    List<Double> jfengPeriodPrice = getCollect(list, ChargeDataByYear::getJfengPeriodPrice);
    List<Double> monthGroups = getCollect(list, ChargeDataByYear::getMonthSum);
    Double monthSum = monthGroups.stream().filter(Objects::nonNull).mapToDouble(o -> o).sum();
    List<Double> monthPriceGroups = getCollect(list, ChargeDataByYear::getMonthElectricPrice);
    Double monthSumPrice = monthPriceGroups.stream().filter(Objects::nonNull).mapToDouble(o -> o).sum();
    Double maxMS = new ArrayList<>(getCollect(list, ChargeDataByYear::getMonthSum)).stream().max(Double::compareTo).orElse(0d);
    Double maxMonthSum = convertPrecision(maxMS);
    String d = list.stream().filter(o -> maxMonthSum.equals(o.getMonthSum())).findFirst().orElse(new ChargeDataByYear()).getMonth();
    String maxMonth = (d == null ? "" : d.replace("-", "/"));
    Map<String, Object> map = new LinkedHashMap<>();

    map.put("date", date);
    map.put("fengVals", fengVals);
    map.put("pingVals", pingVals);
    map.put("guVals", guVals);
    map.put("jfengVals", jfengVals);
    map.put("fengPeriodPrice", fengPeriodPrice);
    map.put("pingPeriodPrice", pingPeriodPrice);
    map.put("guPeriodPrice", guPeriodPrice);
    map.put("jfengPeriodPrice", jfengPeriodPrice);
    //当年月用电量分组
    map.put("monthGroup", monthGroups);
    //当年月电价分组
    map.put("monthPriceGroup", monthPriceGroups);
    //当年总用电量
    map.put("monthSum", convertPrecision(monthSum));
    //当年总电价
    map.put("monthSumPrice", convertPrecision(monthSumPrice));
    map.put("maxMonthSum", maxMonthSum);
    map.put("maxMonth", maxMonth);

    return Result.getSuccessResultInfo(map);
  }

  private List<Double> getCollect(List<ChargeDataByYear> list, Function<ChargeDataByYear, Double> func) {
    return list.parallelStream().filter(Objects::nonNull).map(func).collect(Collectors.toList());
  }

  private Double convertPrecision(Double x) {
    DecimalFormat df = new DecimalFormat("#.00");
    return Double.valueOf(df.format(x));
  }

}
