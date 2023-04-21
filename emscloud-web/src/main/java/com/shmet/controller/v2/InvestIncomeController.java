package com.shmet.controller.v2;

import com.google.common.collect.Lists;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.InvestIncomeService;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * 投资收益 --> 投资收益 下的 按天按月按年 分组 计算 图表上方的 光 风 储能收益 总收益
 * eg. 1 按天显示 横坐标为每小时 纵坐标为 光电收益 风电收益 储能收益 总收益
 * eg. 2 按月显示 横坐标为每一天 纵坐标为 光电收益 风电收益 储能收益 总收益
 * eg. 3 按年显示 横坐标为每个月 纵坐标为 光电收益 风电收益 储能收益 总收益
 */
@RestController
@RequestMapping("/v2")
public class InvestIncomeController {

  @Autowired
  private InvestIncomeService investIncomeService;

  // dateType:1 按月 dateType:2 按年
  //计算图表上方的 的 光电收益 风电收益 储能收益 总收益
  @GetMapping("/invest/income")
  @UserLoginToken
  public Result testB(@RequestParam(required = false) String date,
                      @RequestParam List<Long> subprojectIds,
                      @RequestParam(required = false, defaultValue = "1") int dateType) {

    Double currentStoreIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 1) == null ? 0d : investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 1);
    Double currentShineElecIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 2) == null ? 0d : investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 2);
    Double currentWindElecIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 3) == null ? 0d : investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 3);
    Double currentTotalIncome = NumberUtil.convert2(currentStoreIncome + currentShineElecIncome + currentWindElecIncome);

    Double beforeStoreIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 1) == null ? 0d : investIncomeService.calcMonthOrYear(getMonthOrYear(date, dateType), subprojectIds, dateType, 1);
    Double beforeShineElecIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 2) == null ? 0d : investIncomeService.calcMonthOrYear(getMonthOrYear(date, dateType), subprojectIds, dateType, 2);
    Double beforeWindElecIncome = investIncomeService.calcMonthOrYear(date, subprojectIds, dateType, 3) == null ? 0d : investIncomeService.calcMonthOrYear(getMonthOrYear(date, dateType), subprojectIds, dateType, 3);
    Double beforeTotalIncome = NumberUtil.convert2(beforeStoreIncome + beforeShineElecIncome + beforeWindElecIncome);

    Map<String, Double> map = new LinkedHashMap<>();
    map.put("currentShineElecIncome", currentShineElecIncome);
    map.put("currentWindElecIncome", currentWindElecIncome);
    map.put("currentStoreIncome", currentStoreIncome);
    map.put("currentTotalIncome", currentTotalIncome);
    map.put("beforeStoreIncome", beforeStoreIncome);
    map.put("beforeShineElecIncome", beforeShineElecIncome);
    map.put("beforeWindElecIncome", beforeWindElecIncome);
    map.put("beforeTotalIncome", beforeTotalIncome);

    return Result.getSuccessResultInfo(map);
  }

  //投资收益 --> 投资收益 按月按年统计 下方图表展示数据接口
  @GetMapping("/group/incomedata")
  @UserLoginToken
  public Result groupYearOrMonthData(@RequestParam(required = false) String date,
                                     @RequestParam List<Long> subprojectIds,
                                     @RequestParam(required = false, defaultValue = "1") int dateType) {
    Map<String, Double> storeIncomeMap = investIncomeService.groupYearOrMonthData(date, subprojectIds, dateType, 1);
    Map<String, Double> shineIncomeMap = investIncomeService.groupYearOrMonthData(date, subprojectIds, dateType, 2);
    Map<String, Double> windIncomeMap = investIncomeService.groupYearOrMonthData(date, subprojectIds, dateType, 3);

    return getResult(shineIncomeMap, windIncomeMap, storeIncomeMap);
  }

  //投资收益--> 投资收益 按月按年统计 下方图表展示数据接口
  @GetMapping("/group/month-and-year")
  @UserLoginToken
  public Result calcMonthAndYearData(@RequestParam(required = false) String date,
                                     @RequestParam Long subprojectId,
                                     @RequestParam(required = false, defaultValue = "1") int dateType,
                                     @RequestParam(required = false, defaultValue = "2") int deviceModelType) {
    return Result.getSuccessResultInfo(investIncomeService.calcMonthAndYearData(date, subprojectId, dateType, deviceModelType));
  }

  //deviceModelType 0 表示市电 1 表示储能 2 表示光电 3 表示风电 4 表示充电桩
  //投资收益 --> 投资收益 按天统计 图表下方数据
  @GetMapping("/calc/daydata")
  @UserLoginToken
  public Result calcDayData(@RequestParam(required = false) String date,
                            @RequestParam List<Long> subprojectIds) {
    Map<String, Double> shineIncomeMap = investIncomeService.calcDayData(date, subprojectIds, 2);
    Map<String, Double> windIncomeMap = investIncomeService.calcDayData(date, subprojectIds, 3);
    Map<String, Double> storeIncomeMap = investIncomeService.calcDayData(date, subprojectIds, 1);

    return getResult(shineIncomeMap, windIncomeMap, storeIncomeMap);
  }

  //date pattern eg. yyyy-MM-dd
  //deviceModelType 0 表示市电 1 表示储能 2 表示光电 3 表示风电 4 表示充电桩
  @GetMapping("/day/topval")
  @UserLoginToken
  public Result calcTopDataByDay(@RequestParam(required = false) String date,
                                 @RequestParam List<Long> subprojectIds) {
    //光伏收益
    Double shineIncome = investIncomeService.calcTopDataByDay(date, subprojectIds, 2);
    //风电收益
    Double windIncome = investIncomeService.calcTopDataByDay(date, subprojectIds, 3);
    //储能收益
    Double storeIncome = investIncomeService.calcTopDataByDay(date, subprojectIds, 1);

    //昨日光伏收益
    Double beforeshineIncome = investIncomeService.calcTopDataByDay(getBeforeDay(date), subprojectIds, 2);
    //昨日风电收益
    Double beforewindIncome = investIncomeService.calcTopDataByDay(getBeforeDay(date), subprojectIds, 3);
    //昨日储能收益
    Double beforestoreIncome = investIncomeService.calcTopDataByDay(getBeforeDay(date), subprojectIds, 1);

    return Result.getSuccessResultInfo(Lists.newArrayList(shineIncome, windIncome, storeIncome, beforeshineIncome, beforewindIncome, beforestoreIncome));
  }

  private String getBeforeDay(String date) {
    if (StringUtils.isBlank(date)) {
      return LocalDate.now().minusDays(1).toString();
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      return LocalDate.parse(date, formatter).minusDays(1).toString();
    }
  }

  private Result getResult(Map<String, Double> shineIncomeMap, Map<String, Double> windIncomeMap, Map<String, Double> storeIncomeMap) {
    List<InvestIncomeVO> vos = new ArrayList<>();
    if (storeIncomeMap.size() >= shineIncomeMap.size() && storeIncomeMap.size() >= windIncomeMap.size()) {
      buildVo(storeIncomeMap, storeIncomeMap, shineIncomeMap, windIncomeMap, vos, 1);
    } else if (storeIncomeMap.size() <= shineIncomeMap.size() && windIncomeMap.size() <= shineIncomeMap.size()) {
      buildVo(shineIncomeMap, shineIncomeMap, storeIncomeMap, windIncomeMap, vos, 2);
    } else {
      buildVo(windIncomeMap, windIncomeMap, storeIncomeMap, shineIncomeMap, vos, 3);
    }
    return Result.getSuccessResultInfo(vos);
  }

  private String getMonthOrYear(String date, int flag) {
    //先转为 yyyy-MM-dd
    if (flag == 1) {
      String tmp = date + "-16";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate newDate = LocalDate.parse(tmp, formatter);
      return StringUtils.substringBeforeLast(newDate.minusMonths(1).toString(), "-");
    }
    if (flag == 2) {
      return String.valueOf(Long.parseLong(date) - 1);
    } else {
      return "";
    }
  }

  public void buildVo(Map<String, Double> map,
                      Map<String, Double> map1,
                      Map<String, Double> map2,
                      Map<String, Double> map3,
                      List<InvestIncomeVO> vos,
                      int idx) {
    for (String key : map.keySet()) {
      InvestIncomeVO vo = new InvestIncomeVO();

      Double v1 = map1.get(key) == null ? 0d : map1.get(key);
      Double v2 = map2.get(key) == null ? 0d : map2.get(key);
      Double v3 = map3.get(key) == null ? 0d : map3.get(key);
      double totalIncome = NumberUtil.convert2(v1 + v2 + v3);

      vo.setTime(key);

      if (idx == 1) {
        vo.setShineIncome(NumberUtil.convert2(v2));
        vo.setWindIncome(NumberUtil.convert2(v3));
        vo.setStoreIncome(NumberUtil.convert2(v1));
      } else if (idx == 2) {
        vo.setShineIncome(NumberUtil.convert2(v1));
        vo.setWindIncome(NumberUtil.convert2(v3));
        vo.setStoreIncome(NumberUtil.convert2(v2));
      } else {
        vo.setShineIncome(NumberUtil.convert2(v3));
        vo.setWindIncome(NumberUtil.convert2(v1));
        vo.setStoreIncome(NumberUtil.convert2(v2));
      }

      vo.setTotalIncome(totalIncome);

      vos.add(vo);
    }
  }

  private static class InvestIncomeVO {
    private String time;
    private Double shineIncome;
    private Double windIncome;
    private Double storeIncome;
    private Double totalIncome;

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public Double getShineIncome() {
      return shineIncome;
    }

    public void setShineIncome(Double shineIncome) {
      this.shineIncome = shineIncome;
    }

    public Double getWindIncome() {
      return windIncome;
    }

    public void setWindIncome(Double windIncome) {
      this.windIncome = windIncome;
    }

    public Double getStoreIncome() {
      return storeIncome;
    }

    public void setStoreIncome(Double storeIncome) {
      this.storeIncome = storeIncome;
    }

    public Double getTotalIncome() {
      return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
      this.totalIncome = totalIncome;
    }
  }
}
