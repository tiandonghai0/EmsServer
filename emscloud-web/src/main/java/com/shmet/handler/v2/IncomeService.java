package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.*;
import com.shmet.ArithUtil;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.ProjectMapper;
import com.shmet.dao.SubProjectMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.Project;
import com.shmet.entity.mysql.gen.SubProject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author
 * V2.1
 */
@Service
public class IncomeService {

  public static final Logger log = LoggerFactory.getLogger(IncomeService.class);

  @Resource
  private ProjectMapper projectMapper;

  @Resource
  private SubProjectMapper subProjectMapper;

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private MongoTemplate mongoTemplate;

  public List<Integer> getProjectIds(Integer customerId) {
    return projectMapper.selectList(new QueryWrapper<Project>().eq("customer_id", customerId))
        .stream().map(Project::getProjectId).collect(Collectors.toList());
  }

  public List<Long> getSubProjectIds(Integer customerId) {
    List<Integer> projectIds = getProjectIds(customerId);
    if (projectIds.size() > 0) {
      return subProjectMapper.selectList(new QueryWrapper<SubProject>().in("project_id", projectIds))
          .stream().map(SubProject::getSubProjectId).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  public List<Long> getDeviceNos(Integer customerId) {
    List<Long> subProjectIds = getSubProjectIds(customerId);
    if (subProjectIds.size() > 0) {
      return deviceMapper.selectList(new QueryWrapper<Device>().in("sub_project_id", subProjectIds)
          .eq("device_model", "1").eq("device_model_type", "1"))
          .stream().map(Device::getDeviceNo).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  /**
   * 日收益 格式必须是yyyy-MM-dd
   *
   * @param day eg. 2020-10-16
   */
  public Double dayIncome(String day, Integer customerId) {
    List<Long> deviceNos = getDeviceNos(customerId);
    Long date = Long.valueOf(StringUtils.replace(day, "-", ""));
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").is(date)),
        Aggregation.project().andExclude("_id")
            .and(ObjectOperators.valueOf("statistics").toArray()).as("statistics"),
        Aggregation.unwind("$statistics"),
        Aggregation.match(Criteria.where("statistics.k").in("EPE", "EPI")),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    double res = ArithUtil.sub(calcIncome(aggRes).getLeft(), calcIncome(aggRes).getRight());
    return convertPrecision(res);
  }

  /**
   * 周收益 格式必须是yyyy-MM-dd
   *
   * @param day eg. 2020-10-12 - 2020-10-16
   */
  public Double weekIncome(String day, Integer customerId) {
    List<Long> deviceNos = getDeviceNos(customerId);
    Long startWeekDay = getStartWeekDay(day);
    Long date = Long.valueOf(StringUtils.replace(day, "-", ""));
    if (startWeekDay > date) {
      date = startWeekDay;
    }
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(startWeekDay).lte(date)),
        Aggregation.project().andExclude("_id")
            .and(ObjectOperators.valueOf("statistics").toArray()).as("statistics"),
        Aggregation.unwind("$statistics"),
        Aggregation.match(Criteria.where("statistics.k").in("EPE", "EPI")),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    double res = ArithUtil.sub(calcIncome(aggRes).getLeft(), calcIncome(aggRes).getRight());
    return convertPrecision(res);
  }

  /**
   * 月收益 格式必须是yyyy-MM
   *
   * @param month eg. 2020-10
   */
  public Double monthIncome(String month, Integer customerId) {
    List<Long> deviceNos = getDeviceNos(customerId);
    Long yearMonth = Long.valueOf(StringUtils.replace(month, "-", ""));
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(yearMonth)),
        Aggregation.project().andExclude("_id")
            .and(ObjectOperators.valueOf("statistics").toArray()).as("statistics"),
        Aggregation.unwind("$statistics"),
        Aggregation.match(Criteria.where("statistics.k").in("EPE", "EPI")),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    double res = ArithUtil.sub(calcIncome(aggRes).getLeft(), calcIncome(aggRes).getRight());
    return convertPrecision(res);
  }

  /**
   * 年收益 格式必须是yyyy
   *
   * @param year eg. 2020
   */
  public Double yearIncome(String year, Integer customerId) {
    List<Long> deviceNos = getDeviceNos(customerId);
    Long startMonth = Long.valueOf(year + "00");
    Long endMonth = Long.valueOf(year + "12");
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gt(startMonth).lte(endMonth)),
        Aggregation.project().andExclude("_id")
            .and(ObjectOperators.valueOf("statistics").toArray()).as("statistics"),
        Aggregation.unwind("$statistics"),
        Aggregation.match(Criteria.where("statistics.k").in("EPE", "EPI")),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    double res = ArithUtil.sub(calcIncome(aggRes).getLeft(), calcIncome(aggRes).getRight());
    return convertPrecision(res);
  }

  /**
   * 总收益 格式必须是系统运行年份至当前年份
   * eg. 2019 - 2029
   */
  public Double totalIncome(Integer customerId) {
    List<Long> deviceNos = getDeviceNos(customerId);
    String sdate = String.valueOf(LocalDate.now());
    Long currentMonth = Long.valueOf(StringUtils.replace(sdate, "-", "").substring(0, 6));
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(201901).lte(currentMonth)),
        Aggregation.project().andExclude("_id")
            .and(ObjectOperators.valueOf("statistics").toArray()).as("statistics"),
        Aggregation.unwind("$statistics"),
        Aggregation.match(Criteria.where("statistics.k").in("EPE", "EPI")),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    double res = ArithUtil.sub(calcIncome(aggRes).getLeft(), calcIncome(aggRes).getRight());
    return convertPrecision(res);
  }

  private static Double convertPrecision(Double x) {
    DecimalFormat df = new DecimalFormat("#.00");
    return Double.valueOf(df.format(x));
  }

  public static Pair<Double, Double> calcIncome(AggregationResults<Document> aggRes) {
    double epePrice = 0d, epiPrice = 0d;
    for (Document doc : aggRes) {
      Map statistics = doc.get("statistics", Map.class);
      String key = (String) statistics.get("k");
      Map<String, Double> vmap = (Map<String, Double>) statistics.get("v");
      Double price = vmap.get("price");
      if (price != null) {
        if (StringUtils.equals("EPE", key)) {
          epePrice = ArithUtil.add(epePrice, price);
        } else {
          epiPrice = ArithUtil.add(epiPrice, price);
        }
      }
    }
    return Pair.of(epePrice, epiPrice);
  }

  /**
   * 获取当前周的周一的日期
   *
   * @return eg 2020.10.16 那一周的周一是 2020.10.12
   */
  private static Long getStartWeekDay(String day) {
    LocalDate date = null;
    try {
      date = LocalDate.parse(day);
    } catch (Exception e) {
      log.error("传入的日期格式错误，无法转换");
    }
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    TemporalAdjuster adjuster = TemporalAdjusters.ofDateAdjuster(o -> o.minusDays(o.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
    return Long.valueOf(df.format(Optional.ofNullable(date).orElse(LocalDate.now()).with(adjuster)).replace("-", ""));
  }
}
