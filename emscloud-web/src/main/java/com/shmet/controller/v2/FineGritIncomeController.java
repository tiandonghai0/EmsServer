package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.EnergySaveEmessionReduceService;
import com.shmet.handler.v2.FineGritIncomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author
 * 计算储能收益 充放电量 充电成本 放电收益 同时 按 各时段 细粒度划分 充放电量
 */
@RestController
@RequestMapping("/v2/store")
public class FineGritIncomeController {
  @Autowired
  private FineGritIncomeService fineGritIncomeService;

  @Autowired
  EnergySaveEmessionReduceService energySaveEmessionReduceService;

  //计算 图表下方的 充放电量 收益 数据接口
  //按天 下方显示 每小时的 充放电量 收益
  //按月 下方显示 每天的 充放电量 收益
  //按年 下方显示 每月的 充放电量 收益
  @GetMapping("/down/datas")
  @UserLoginToken
  public Result downDatas(@RequestParam(required = false) String date,
                          @RequestParam Long subprojectId,
                          @RequestParam(required = false, defaultValue = "1") int dateType) {
    return Result.getSuccessResultInfo(fineGritIncomeService.calcEpeAndEpiAndIncome(date, subprojectId, dateType));
  }

  //计算 图表上方的 充电量 充电成本 放电量 放电收益 总收益接口
  //按天 显示 每天的 充电量 充电成本 放电量 放电收益 总收益 以及昨天的 充电量 充电成本 放电量 放电收益 总收益
  //按月 显示 每月的 充电量 充电成本 放电量 放电收益 总收益 以及上月的 充电量 充电成本 放电量 放电收益 总收益
  //按年 显示 每年的 充电量 充电成本 放电量 放电收益 总收益 以及去年的 充电量 充电成本 放电量 放电收益 总收益
  @GetMapping("/up/val")
  @UserLoginToken
  public Result upVal(@RequestParam(required = false) String date,
                      @RequestParam Long subprojectId,
                      @RequestParam(required = false, defaultValue = "1") int dateType) {
    return Result.getSuccessResultInfo(fineGritIncomeService.calcNavigateVal(date, subprojectId, dateType));
  }

  //计算 昨天/上月/上年 的 充电量 充电成本 放电量 放电收益 总收益 实际可以废弃
  @GetMapping("/testc")
  @UserLoginToken
  public Result testC(@RequestParam(required = false) String date,
                      @RequestParam Long subprojectId,
                      @RequestParam(required = false, defaultValue = "1") int dateType) {
    return Result.getSuccessResultInfo(fineGritIncomeService.calcBeforeDayOrMonthOrYear(date, subprojectId, dateType));
  }

  //计算节能减排 下方的 图表数据 (发电量 节煤 节水 COS减排量 SO2减排量)
  //按月 显示每天的 发电量 节煤 节水 COS减排量 SO2减排量
  //按年 显示每月的 发电量 节煤 节水 COS减排量 SO2减排量
  @GetMapping("/save-energy/down")
  @UserLoginToken
  public Result saveEnergyDown(@RequestParam(required = false) String date,
                               @RequestParam Long subprojectId,
                               @RequestParam(required = false, defaultValue = "1") int dateType) {
    return Result.getSuccessResultInfo(energySaveEmessionReduceService.calcEchartDatas(date, subprojectId, dateType));
  }

  //计算节能减排 上方的 数据 (发电量 节煤 节水 COS减排量 SO2减排量)
  @GetMapping("/save-energy/up")
  @UserLoginToken
  public Result saveEnergyUp(@RequestParam(required = false) String date,
                             @RequestParam Long subprojectId,
                             @RequestParam(required = false, defaultValue = "1") int dateType) {
    Map<String, Object> currentMap = energySaveEmessionReduceService.calcEchartTopDatas(date, subprojectId, dateType);
    Map<String, Object> beforeMap = energySaveEmessionReduceService.calcEchartTopDatas(getBeforeMonthOrYear(date, dateType), subprojectId, dateType);

    String currentepesum = String.valueOf(currentMap.get("epesum") == null ? 0d : currentMap.get("epesum"));
    String beforeepesum = String.valueOf(beforeMap.get("epesum") == null ? 0d : beforeMap.get("epesum"));
    BigDecimal epesumsub = new BigDecimal(currentepesum).subtract(new BigDecimal(beforeepesum));

    String currentcoal = String.valueOf(currentMap.get("coal") == null ? 0d : currentMap.get("coal"));
    String beforecoal = String.valueOf(beforeMap.get("coal") == null ? 0d : beforeMap.get("coal"));
    BigDecimal coalsub = new BigDecimal(currentcoal).subtract(new BigDecimal(beforecoal));

    String currentwater = String.valueOf(currentMap.get("water") == null ? 0d : currentMap.get("water"));
    String beforewater = String.valueOf(beforeMap.get("water") == null ? 0d : beforeMap.get("water"));
    BigDecimal watersub = new BigDecimal(currentwater).subtract(new BigDecimal(beforewater));

    String currentco2 = String.valueOf(currentMap.get("co2") == null ? 0d : currentMap.get("co2"));
    String beforeco2 = String.valueOf(beforeMap.get("co2") == null ? 0d : beforeMap.get("co2"));
    BigDecimal co2sub = new BigDecimal(currentco2).subtract(new BigDecimal(beforeco2));

    String currentso2 = String.valueOf(currentMap.get("so2") == null ? 0d : currentMap.get("so2"));
    String beforeso2 = String.valueOf(beforeMap.get("so2") == null ? 0d : beforeMap.get("so2"));
    BigDecimal so2sub = new BigDecimal(currentso2).subtract(new BigDecimal(beforeso2));

    currentMap.put("epesumsub", epesumsub);
    currentMap.put("coalsub", coalsub);
    currentMap.put("watersub", watersub);
    currentMap.put("co2sub", co2sub);
    currentMap.put("so2sub", so2sub);

    return Result.getSuccessResultInfo(currentMap);
  }

  //计算上一个月 或者是上一年
  private String getBeforeMonthOrYear(String date, int dateType) {
    //按月
    if (dateType == 1) {
      if (StringUtils.isNotBlank(date)) {
        //先转为 yyyy-MM-dd
        String tmp = date + "-16";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(tmp, formatter);
        return StringUtils.substringBeforeLast(newDate.minusMonths(1).toString(), "-");
      } else {
        return StringUtils.substringBeforeLast(LocalDate.now().minusMonths(1).toString(), "-");
      }
    }
    //按年
    if (dateType == 2) {
      if (StringUtils.isNotBlank(date)) {
        return String.valueOf(Long.parseLong(date) - 1);
      } else {
        return String.valueOf(Year.now().getValue() - 1);
      }
    }

    return "-1";
  }

}
