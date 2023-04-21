package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.ArithUtil;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 计算投资收益 --> 投资收益 下的 光电收益 风电收益 储能收益 总收益
 * eg. 1 按天显示 横坐标为每小时 纵坐标为 光电收益 风电收益 储能收益 总收益
 * eg. 2 按月显示 横坐标为每一天 纵坐标为 光电收益 风电收益 储能收益 总收益
 * eg. 3 按年显示 横坐标为每个月 纵坐标为 光电收益 风电收益 储能收益 总收益
 */
@Service
public class InvestIncomeService {

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private MongoTemplate mongoTemplate;

  //date pattern eg. yyyy-MM-dd
  //deviceModelType 0 表示市电 1 表示储能 2 表示光电 3 表示风电 4 表示充电桩
  public Map<String, Double> calcDayData(String date, List<Long> subprojectIds, int deviceModelType) {
    AggregationResults<Document> aggRes = calcDayOriginDatas(date, subprojectIds, deviceModelType);
    Map<String, Double> map = new LinkedHashMap<>();
    for (Document doc : aggRes.getMappedResults()) {
      String hour = String.valueOf(doc.get("hour"));
      double epeprice = Double.parseDouble(String.valueOf(doc.get("epeprice") == null ? 0d : doc.get("epeprice")));
      double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice") == null ? 0d : doc.get("epiprice")));
      if (subprojectIds.contains(20202002001L) && deviceModelType == 1) {
        calcMapVal(map, hour, ArithUtil.sub(epiprice, epeprice));
      } else if (subprojectIds.contains(20202002001L)) {
        calcMapVal(map, hour, ArithUtil.add(Math.abs(epiprice), epeprice));
      } else {
        calcMapVal(map, hour, epeprice);
      }

    }

    return map;
  }

  //date pattern eg. yyyy-MM-dd
  //投资收益 --> 投资收益 (上方的光电收益 风电收益 储能收益 总收益) 按天
  public Double calcTopDataByDay(String date, List<Long> subprojectIds, int deviceModelType) {
    Pair<Long, Long> pair = dateToPair(date);
    List<Long> deviceNos = getDeviceNosBySubprojectIds(subprojectIds, deviceModelType);

    ProjectionOperation projectionOperation = null;
    GroupOperation groupOperation = null;

    if (subprojectIds.contains(20202002001L)) {
      projectionOperation = Aggregation.project().andExclude("_id")
          .and(StringOperators.Substr.valueOf("hour").substring(0, 8)).as("day")
          .and("$statistics.EPE.price").as("epeprice").and("$statistics.EPI.price").as("epiprice");
      groupOperation = Aggregation.group("day").sum("epeprice").as("income").sum("epiprice").as("epiincome");
    }

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
        projectionOperation,
        groupOperation
    );

    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    for (Document doc : aggRes.getMappedResults()) {
      String sv = String.valueOf(doc.get("income") == null ? 0d : doc.get("income"));
      String epiincome = String.valueOf(doc.get("epiincome") == null ? 0d : doc.get("epiincome"));
      if (subprojectIds.contains(20202002001L) && deviceModelType == 1) {
        return NumberUtil.convert2(Math.abs(ArithUtil.sub(Math.abs(Double.parseDouble(sv)), Math.abs(Double.parseDouble(epiincome)))));
      }
      return NumberUtil.convert2(ArithUtil.add(Math.abs(Double.parseDouble(sv)), Math.abs(Double.parseDouble(epiincome))));
      //return NumberUtil.convert2(Double.parseDouble(sv));
    }
    return 0d;
  }

  //calc origindatas group by day
  public AggregationResults<Document> calcDayOriginDatas(String date, List<Long> subprojectIds, int deviceModelType) {
    Pair<Long, Long> pair = dateToPair(date);
    List<Long> deviceNos = getDeviceNosBySubprojectIds(subprojectIds, deviceModelType);

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
        Aggregation.project("hour")
            .and("$statistics.EPE.price").as("epeprice")
            .and("$statistics.EPI.price").as("epiprice"),
        Aggregation.sort(Sort.Direction.ASC, "hour")
    );

    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  //date flag:1 pattern eg. yyyy-MM
  //date flag:2 pattern eg. yyyy
  public Double calcMonthOrYear(String date, List<Long> subprojectIds, int dateType, int deviceModelType) {
    Long time = dateToLong(date);
    List<Long> deviceNos = getDeviceNosBySubprojectIds(subprojectIds, deviceModelType);

    MatchOperation conditionMatch = null;

    //按月
    if (dateType == 1) {
      conditionMatch = Aggregation.match(Criteria.where("yearMonth").is(time));
    } else if (dateType == 2) {
      long startMonth = Long.parseLong(time + "01");
      long endMonth = Long.parseLong(time + "12");
      conditionMatch = Aggregation.match(Criteria.where("yearMonth").gte(startMonth).lte(endMonth));
    }

    MatchOperation matchOpt = Aggregation.match(Criteria.where("deviceNo").in(deviceNos));

    ProjectionOperation projectOpt = Aggregation.project().andExclude("_id")
        .and("$statistics.EPE.price").as("EPEIncome")
        .and("$statistics.EPI.price").as("EPIIncome");

    Aggregation agg = Aggregation.newAggregation(matchOpt, conditionMatch, projectOpt);
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);

    Map<String, Double> map = new LinkedHashMap<>();

    for (Document doc : aggRes.getMappedResults()) {
      double epeIncome = Double.parseDouble(String.valueOf(doc.get("EPEIncome") == null ? 0d : doc.get("EPEIncome")));
      double epiIncome = Double.parseDouble(String.valueOf(doc.get("EPIIncome") == null ? 0d : doc.get("EPIIncome")));

      calcMapVal(map, "epeIncome", epeIncome);
      calcMapVal(map, "epiIncome", epiIncome);
    }

    //double income = Math.abs(ArithUtil.sub(map.get("epeIncome") == null ? 0d : map.get("epeIncome"), map.get("epiIncome") == null ? 0d : map.get("epiIncome")));
    double income;
    if (deviceModelType == 1) {
      income = ArithUtil.sub(Math.abs(map.get("epiIncome") == null ? 0d : map.get("epiIncome")), Math.abs(map.get("epeIncome") == null ? 0d : map.get("epeIncome")));
    } else {
      income = ArithUtil.add(Math.abs(map.get("epeIncome") == null ? 0d : map.get("epeIncome")), Math.abs(map.get("epiIncome") == null ? 0d : map.get("epiIncome")));
    }
    return NumberUtil.convert2(income);
  }

  //date flag:1 pattern eg. yyyy-MM
  //date flag:2 pattern eg. yyyy
  public Map<String, Double> groupYearOrMonthData(String date, List<Long> subprojectIds, int dateType, int deviceModelType) {
    Long time = dateToLong(date);
    List<Long> deviceNos = getDeviceNosBySubprojectIds(subprojectIds, deviceModelType);

    Map<String, Double> map = new LinkedHashMap<>();
    AggregationResults<Document> aggRes;
    if (dateType == 1) {
      long startDay = Long.parseLong(time + "01");
      long endDay = Long.parseLong(time + "31");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(startDay).lte(endDay)),
          Aggregation.project("day").andExclude("_id")
              .and("$statistics.EPE.price").as("EPEPrice")
              .and("$statistics.EPI.price").as("EPIPrice"),
          Aggregation.group("day")
              .sum("EPEPrice").as("EPEIncome")
              .sum("EPIPrice").as("EPIIncome"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
      calcAggToMap(aggRes, map, deviceModelType);
    } else if (dateType == 2) {
      long startMonth = Long.parseLong(time + "01");
      long endMonth = Long.parseLong(time + "12");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(startMonth).lte(endMonth)),
          Aggregation.project("yearMonth").andExclude("_id")
              .and("$statistics.EPE.price").as("EPEPrice")
              .and("$statistics.EPI.price").as("EPIPrice"),
          Aggregation.group("yearMonth")
              .sum("EPEPrice").as("EPEIncome")
              .sum("EPIPrice").as("EPIIncome"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
      calcAggToMap(aggRes, map, deviceModelType);
    }
    return map;
  }

  //0 表示市电 1 表示储能 2 表示光电 3 表示风电 4 表示充电桩
  public List<Long> getDeviceNosBySubprojectId(Long subprojectId, int type) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("device_model_type", type)
            .eq("device_model", 1)
            .eq("sub_project_id", subprojectId)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  //type 0 表示市电 1 表示储能 2 表示光电 3 表示风电 4 表示充电桩
  public List<Long> getDeviceNosBySubprojectIds(List<Long> subprojectIds, int type) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("device_model_type", type)
            .eq("device_model", 1)
            .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  public void calcAggToMap(AggregationResults<Document> aggRes, Map<String, Double> map, int deviceModelType) {
    if (aggRes.getMappedResults().size() > 0) {
      for (Document doc : aggRes.getMappedResults()) {
        String hour = String.valueOf(doc.get("_id") == null ? "" : doc.get("_id"));
        double epeIncome = Double.parseDouble(String.valueOf(doc.get("EPEIncome") == null ? 0d : doc.get("EPEIncome")));
        double epiIncome = Double.parseDouble(String.valueOf(doc.get("EPIIncome") == null ? 0d : doc.get("EPIIncome")));
        if (deviceModelType == 1) {
          map.put(hour, NumberUtil.convert2(Math.abs(ArithUtil.sub(Math.abs(epeIncome), Math.abs(epiIncome)))));
        } else {
          map.put(hour, NumberUtil.convert2(ArithUtil.add(Math.abs(epeIncome), Math.abs(epiIncome))));
        }
      }
    }
  }

  //仅针对 天
  public Pair<Long, Long> dateToPair(String date) {
    //为空 默认为当前日期
    if (StringUtils.isBlank(date)) {
      String sdate = LocalDate.now().toString();
      String time = StringUtils.replace(sdate, "-", "");
      long startTime = Long.parseLong(time + "00");
      long endTime = Long.parseLong(time + "24");
      return Pair.of(startTime, endTime);
    } else {
      String sdate = StringUtils.replace(date, "-", "");
      long startTime = Long.parseLong(sdate + "00");
      long endTime = Long.parseLong(sdate + "24");
      return Pair.of(startTime, endTime);
    }
  }

  //仅针对 月 年
  public Long dateToLong(String date) {
    //为空 默认为当前日期
    if (StringUtils.isBlank(date)) {
      String localMonthStr = StringUtils.substringBeforeLast(LocalDate.now().toString(), "-");
      return Long.parseLong(StringUtils.replace(localMonthStr, "-", ""));
    } else {
      return Long.parseLong(StringUtils.replace(date, "-", ""));
    }
  }

  //通用性
  public void calcMapVal(Map<String, Double> map, String str, Double d) {
    if (map.get(str) != null) {
      map.put(str, ArithUtil.add(map.get(str), d));
    } else {
      map.put(str, d);
    }
  }

  //安康华银 按月/年  查询 各时段 发电量
  //deviceModelType 0 市电 1 储能电表 2 光电电表 3 风电 4 充电桩
  public Map<String, Double> calcMonthAndYearData(String date, Long subprojectId, int dateType, int deviceModelType) {
    Long time = dateToLong(date);
    List<Long> deviceNos = getDeviceNosBySubprojectId(subprojectId, deviceModelType);

    MatchOperation conditionMatch = null;
    //按月
    if (dateType == 1) {
      long startTime = Long.parseLong(time + "0100");
      long endTime = Long.parseLong(time + "3200");
      conditionMatch = Aggregation.match(Criteria.where("hour").gte(startTime).lte(endTime));
    }

    //按年
    if (dateType == 2) {
      long startTime = Long.parseLong(StringUtils.substring(time.toString(), 0, 4) + "010100");
      long endTime = Long.parseLong(StringUtils.substring(time.toString(), 0, 4) + "123200");
      conditionMatch = Aggregation.match(Criteria.where("hour").gte(startTime).lte(endTime));
    }

    MatchOperation matchOpt = Aggregation.match(Criteria.where("deviceNo").in(deviceNos));
    ProjectionOperation projectOpt = Aggregation.project().andExclude("_id")
        .and("$statistics.EPE.priceType").as("pricetype")
        .and("$statistics.EPE.diffsum").as("epediffsum");
    GroupOperation groupOpt = Aggregation.group("pricetype")
        .sum("epediffsum").as("epesum");

    Aggregation agg = Aggregation.newAggregation(matchOpt, conditionMatch, projectOpt, groupOpt);

    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);

    Map<String, Double> map = new LinkedHashMap<>();

    for (Document doc : aggRes.getMappedResults()) {
      //1: 峰电 2 谷电 3: 平电 4: 尖峰段电
      Object obj = doc.get("_id");
      if (obj != null) {
        String type = String.valueOf(obj);
        Double v = Double.valueOf(String.valueOf(doc.get("epesum")));
        map.put(type, NumberUtil.convert2(v));
      }

    }

    return map;
  }
}
