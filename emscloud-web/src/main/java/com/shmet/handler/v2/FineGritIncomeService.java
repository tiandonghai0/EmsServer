package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.shmet.ArithUtil;
import com.shmet.DateTimeUtils;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 计算储能收益 充放电量 充电成本 放电收益
 * eg. 1 按天显示 横坐标为每小时 纵坐标为 充放电量 小时收益
 * eg. 2 按月显示 横坐标为每一天 纵坐标为 充放电量 每天收益
 * eg. 3 按年显示 横坐标为每个月 纵坐标为 充放电量 每月收益
 */
@Service
public class FineGritIncomeService {

  public static final Logger log = LoggerFactory.getLogger(FineGritIncomeService.class);

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private MongoTemplate mongoTemplate;


  public Map<String, Object> calcNavigateVal(String date, Long subprojectId, int flag) {
    Map<String, Object> navigateMap = new LinkedHashMap<>();
    //获取原始数据
    AggregationResults<Document> aggRes = getOriginDatas(date, subprojectId, flag);
    Map<String, Double> beforeMap = calcBeforeDayOrMonthOrYear(date, subprojectId, flag);
    if (aggRes != null) {
      Double epesum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epediffsum")).reduce(Double::sum).orElse(0d);
      Double episum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epidiffsum")).reduce(Double::sum).orElse(0d);
      Double epesumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epeprice")))).reduce(Double::sum).orElse(0d);
      Double episumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epiprice")))).reduce(Double::sum).orElse(0d);

      //安康华银储能特殊处理
      if (subprojectId == 20202002001L) {
        //epi 放电
        navigateMap.put("episum", NumberUtil.convert2(epesum));
        navigateMap.put("epiprice", NumberUtil.convert2(epesumprice));
        navigateMap.put("epesum", NumberUtil.convert2(episum));
        navigateMap.put("epeprice", NumberUtil.convert2(episumprice));
        navigateMap.put("income", NumberUtil.convert2(ArithUtil.sub(episumprice, epesumprice)));
        navigateMap.put("episubval", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epesum")), objToDouble(beforeMap.get("epesum")))));
        navigateMap.put("episubprice", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epeprice")), objToDouble(beforeMap.get("epeprice")))));
        navigateMap.put("epesubval", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("episum")), objToDouble(beforeMap.get("episum")))));
        navigateMap.put("epesubprice", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epiprice")), objToDouble(beforeMap.get("epiprice")))));
      } else {
        //其余 默认都是 epe 放电
        navigateMap.put("episum", NumberUtil.convert2(episum));
        navigateMap.put("epiprice", NumberUtil.convert2(episumprice));
        navigateMap.put("epesum", NumberUtil.convert2(epesum));
        navigateMap.put("epeprice", NumberUtil.convert2(epesumprice));
        navigateMap.put("income", NumberUtil.convert2(ArithUtil.sub(epesumprice, episumprice)));
        navigateMap.put("episubval", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("episum")), objToDouble(beforeMap.get("episum")))));
        navigateMap.put("episubprice", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epiprice")), objToDouble(beforeMap.get("epiprice")))));
        navigateMap.put("epesubval", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epesum")), objToDouble(beforeMap.get("epesum")))));
        navigateMap.put("epesubprice", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("epeprice")), objToDouble(beforeMap.get("epeprice")))));
        navigateMap.put("subincome", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("income")), objToDouble(beforeMap.get("income")))));
      }
      navigateMap.put("subincome", NumberUtil.convert2(ArithUtil.sub(objToDouble(navigateMap.get("income")), objToDouble(beforeMap.get("income")))));

    }

    return navigateMap;
  }

  /**
   * 去掉对比上一日月年数据
   * @param date
   * @param subprojectId
   * @param flag
   * @return
   */
  public double calcNavigateValV2(String date, Long subprojectId, int flag) {
    Map<String, Object> navigateMap = new LinkedHashMap<>();
    //获取原始数据
    AggregationResults<Document> aggRes = getOriginDatas(date, subprojectId, flag);
    if (aggRes != null) {
      Double epesum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epediffsum")).reduce(Double::sum).orElse(0d);
      Double episum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epidiffsum")).reduce(Double::sum).orElse(0d);
      Double epesumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epeprice")))).reduce(Double::sum).orElse(0d);
      Double episumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epiprice")))).reduce(Double::sum).orElse(0d);

        //其余 默认都是 epe 放电
      return NumberUtil.convert2(ArithUtil.sub(epesumprice, episumprice));
    }

    return 0.0d;
  }

  public JSONObject storedEnergyMonthEpe(String date, List<Long> deviceNos, int flag) {
    JSONObject json=new JSONObject();
    //获取原始数据
    AggregationResults<Document> aggRes = getOriginDatasV2(date, deviceNos, flag);
    if (aggRes != null) {
      Double epesum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epediffsum")).reduce(Double::sum).orElse(0d);
      Double episum = aggRes.getMappedResults().stream().map(d -> d.getDouble("epidiffsum")).reduce(Double::sum).orElse(0d);
      Double epesumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epeprice")))).reduce(Double::sum).orElse(0d);
      Double episumprice = aggRes.getMappedResults().stream().map(d -> Double.parseDouble(String.valueOf(d.get("epiprice")))).reduce(Double::sum).orElse(0d);
      Double income=NumberUtil.convert2(ArithUtil.sub(epesumprice, episumprice));
      //其余 默认都是 epe 放电
      //return NumberUtil.convert2(ArithUtil.sub(epesumprice, episumprice));

      json.put("epesum",NumberUtil.convert2(epesum));
      json.put("episum",NumberUtil.convert2(episum));
      json.put("epesumprice",NumberUtil.convert2(epesumprice));
      json.put("episumprice",NumberUtil.convert2(episumprice));
      json.put("income",NumberUtil.convert2(income));

    }

    return json;
  }

  private Double objToDouble(Object obj) {
    return obj == null ? 0d : (Double) obj;
  }

  public List<Map<String, Object>> calcEpeAndEpiAndIncome(String date, Long subprojectId, int flag) {
    List<Map<String, Object>> lists = new ArrayList<>();
    AggregationResults<Document> aggRes = getOriginDatas(date, subprojectId, flag);
    for (Document doc : aggRes) {
      Map<String, Object> map = Maps.newLinkedHashMap();
      String time = String.valueOf(doc.get("_id"));
      double epediffsum = Double.parseDouble(String.valueOf(doc.get("epediffsum")));
      double epidiffsum = Double.parseDouble(String.valueOf(doc.get("epidiffsum")));
      double epeprice = Double.parseDouble(String.valueOf(doc.get("epeprice")));
      double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice")));

      map.put("time", time);
      if (subprojectId == 20202002001L) {
        map.put("epediffsum", NumberUtil.convert2(epidiffsum));
        map.put("epidiffsum", NumberUtil.convert2(epediffsum));
        map.put("income", NumberUtil.convert2(ArithUtil.sub(epiprice, epeprice)));
      } else {
        map.put("epediffsum", NumberUtil.convert2(epediffsum));
        map.put("epidiffsum", NumberUtil.convert2(epidiffsum));
        map.put("income", NumberUtil.convert2(ArithUtil.sub(epeprice, epiprice)));
      }

      lists.add(map);
    }
    return lists;
  }

  //flag: 1:day 2:month 3:year 4总量,统计收益以及充放电量
  public AggregationResults<Document> getOriginDatas(String date, Long subprojectId, int flag) {
    Pair<Long, Long> pair = dateToPair(date, flag);
    List<Long> deviceNos = getStoreEnergyDeviceNosBySubprojectIds(subprojectId);

    if (flag == 1) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
          Aggregation.project().andExclude("_id")
              .and(StringOperators.Substr.valueOf("$hour").substring(8, 2)).as("hour")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice"),
          Aggregation.group("hour")
              .sum("epediffsum").as("epediffsum")
              .sum("epidiffsum").as("epidiffsum")
              .sum("epeprice").as("epeprice")
              .sum("epiprice").as("epiprice"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    } else if (flag == 2) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(pair.getLeft()).lte(pair.getRight())),
          Aggregation.project("day").andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice"),
          Aggregation.group("day")
              .sum("epediffsum").as("epediffsum")
              .sum("epidiffsum").as("epidiffsum")
              .sum("epeprice").as("epeprice")
              .sum("epiprice").as("epiprice"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    } else if (flag == 3) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(pair.getLeft()).lte(pair.getRight())),
          Aggregation.project("yearMonth").andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice"),
          Aggregation.group("yearMonth")
              .sum("epediffsum").as("epediffsum")
              .sum("epidiffsum").as("epidiffsum")
              .sum("epeprice").as("epeprice")
              .sum("epiprice").as("epiprice"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    return null;
  }

  //flag: 1:day 2:month 3:year ,4总 ,统计收益以及充放电量 根据deviceNos
  public AggregationResults<Document> getOriginDatasV2(String date, List<Long> deviceNos, int flag) {
    Pair<Long, Long> pair = dateToPair(date, flag);
    //List<Long> deviceNos = getStoreEnergyDeviceNosBySubprojectIds(subprojectId);

    if (flag == 1) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
              Aggregation.project().andExclude("_id")
                      .and(StringOperators.Substr.valueOf("$hour").substring(8, 2)).as("hour")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice"),
              Aggregation.group("hour")
                      .sum("epediffsum").as("epediffsum")
                      .sum("epidiffsum").as("epidiffsum")
                      .sum("epeprice").as("epeprice")
                      .sum("epiprice").as("epiprice"),
              Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    } else if (flag == 2) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(pair.getLeft()).lte(pair.getRight())),
              Aggregation.project("day").andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice"),
              Aggregation.group("day")
                      .sum("epediffsum").as("epediffsum")
                      .sum("epidiffsum").as("epidiffsum")
                      .sum("epeprice").as("epeprice")
                      .sum("epiprice").as("epiprice"),
              Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    } else if (flag == 3) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(pair.getLeft()).lte(pair.getRight())),
              Aggregation.project("yearMonth").andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice"),
              Aggregation.group("yearMonth")
                      .sum("epediffsum").as("epediffsum")
                      .sum("epidiffsum").as("epidiffsum")
                      .sum("epeprice").as("epeprice")
                      .sum("epiprice").as("epiprice"),
              Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }else if (flag == 4) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(pair.getLeft())),
              Aggregation.project("yearMonth").andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice"),
              Aggregation.group("yearMonth")
                      .sum("epediffsum").as("epediffsum")
                      .sum("epidiffsum").as("epidiffsum")
                      .sum("epeprice").as("epeprice")
                      .sum("epiprice").as("epiprice"),
              Aggregation.sort(Sort.Direction.ASC, "_id")
      );

      return mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    return null;
  }

//  public int getEnergyProjectCount(List<Long> deviceNo,String date){
//    Pair<Long, Long> pair = dateToPair(date, 4);
//    Aggregation agg = Aggregation.newAggregation(
//            Aggregation.match(Criteria.where("deviceNo").in(deviceNo).and("$statistics.EPE.diffsum").exists(true).and("yearMonth").gte(pair.getLeft())),
//            Aggregation.project().andExclude("_id")
//    );
//    return mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
//  }

  public Map<String, Double> calcBeforeDayOrMonthOrYear(String date, Long subprojectId, int flag) {
    Map<String, Double> map = new LinkedHashMap<>();
    Long before = getBeforeDayOrMonthOrYear(date, flag);
    List<Long> deviceNos = getStoreEnergyDeviceNosBySubprojectIds(subprojectId);

    AggregationResults<Document> aggRes = null;

    if (flag == 1) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").is(before)),
          Aggregation.project().andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    } else if (flag == 2) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(before)),
          Aggregation.project().andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    } else if (flag == 3) {
      Long start = Long.parseLong(before + "01");
      Long end = Long.parseLong(before + "12");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(start).lte(end)),
          Aggregation.project().andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("epeprice")
              .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    if (aggRes != null) {
      for (Document doc : aggRes.getMappedResults()) {
        double epesum = Double.parseDouble(String.valueOf(doc.get("epediffsum")));
        double episum = Double.parseDouble(String.valueOf(doc.get("epidiffsum")));
        double epeprice = Double.parseDouble(String.valueOf(doc.get("epeprice") == null ? 0d : doc.get("epeprice")));
        double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice") == null ? 0d : doc.get("epiprice")));

        map.put("episum", NumberUtil.convert2(ArithUtil.add(episum, map.get("episum") == null ? 0d : map.get("episum"))));
        map.put("epiprice", NumberUtil.convert2(ArithUtil.add(epiprice, map.get("epiprice") == null ? 0d : map.get("epiprice"))));
        map.put("epesum", NumberUtil.convert2(ArithUtil.add(epesum, map.get("epesum") == null ? 0d : map.get("epesum"))));
        map.put("epeprice", NumberUtil.convert2(ArithUtil.add(epeprice, map.get("epeprice") == null ? 0d : map.get("epeprice"))));
      }
      map.put("income", NumberUtil.convert2(ArithUtil.sub(map.get("epeprice") == null ? 0d : map.get("epeprice"), map.get("epiprice") == null ? 0d : map.get("epiprice"))));
    }
    return map;

  }

  public Map<String, Double> calcBeforeDayOrMonthOrYear(String date, List<Long> deviceNos, int flag) {
    Map<String, Double> map = new LinkedHashMap<>();
    Long before = getBeforeDayOrMonthOrYear(date, flag);

    AggregationResults<Document> aggRes = null;

    if (flag == 1) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").is(before)),
              Aggregation.project().andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    } else if (flag == 2) {
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(before)),
              Aggregation.project().andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    } else if (flag == 3) {
      Long start = Long.parseLong(before + "01");
      Long end = Long.parseLong(before + "12");
      Aggregation agg = Aggregation.newAggregation(
              Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(start).lte(end)),
              Aggregation.project().andExclude("_id")
                      .and("$statistics.EPE.diffsum").as("epediffsum")
                      .and("$statistics.EPI.diffsum").as("epidiffsum")
                      .and("$statistics.EPE.price").as("epeprice")
                      .and("$statistics.EPI.price").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    if (aggRes != null) {
      for (Document doc : aggRes.getMappedResults()) {
        double epesum = Double.parseDouble(String.valueOf(doc.get("epediffsum")));
        double episum = Double.parseDouble(String.valueOf(doc.get("epidiffsum")));
        double epeprice = Double.parseDouble(String.valueOf(doc.get("epeprice") == null ? 0d : doc.get("epeprice")));
        double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice") == null ? 0d : doc.get("epiprice")));

        map.put("episum", NumberUtil.convert2(ArithUtil.add(episum, map.get("episum") == null ? 0d : map.get("episum"))));
        map.put("epiprice", NumberUtil.convert2(ArithUtil.add(epiprice, map.get("epiprice") == null ? 0d : map.get("epiprice"))));
        map.put("epesum", NumberUtil.convert2(ArithUtil.add(epesum, map.get("epesum") == null ? 0d : map.get("epesum"))));
        map.put("epeprice", NumberUtil.convert2(ArithUtil.add(epeprice, map.get("epeprice") == null ? 0d : map.get("epeprice"))));
      }
      map.put("income", NumberUtil.convert2(ArithUtil.sub(map.get("epeprice") == null ? 0d : map.get("epeprice"), map.get("epiprice") == null ? 0d : map.get("epiprice"))));
    }
    return map;

  }


  public List<Long> getStoreEnergyDeviceNosBySubprojectIds(Long subprojectId) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("device_model_type", 1)
            .eq("device_model", 1)
            .eq("sub_project_id", subprojectId)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  //day: pattern yyyy-MM-dd flag: 1
  //month: pattern yyyy-MM flag: 2
  //year: pattern yyyy flag: 3
  public Pair<Long, Long> dateToPair(String date, int flag) {
    if (StringUtils.isNotBlank(date)) {
      if (flag == 1) {
        long start = Long.parseLong(StringUtils.replace(date, "-", "") + "00");
        long end = Long.parseLong(StringUtils.replace(date, "-", "") + "24");
        return Pair.of(start, end);
      } else if (flag == 2) {
        long start = Long.parseLong(StringUtils.replace(date, "-", "") + "00");
        long end = Long.parseLong(StringUtils.replace(date, "-", "") + "32");
        return Pair.of(start, end);
      } else if (flag == 3) {
        long start = Long.parseLong(date + "01");
        long end = Long.parseLong(date + "12");
        return Pair.of(start, end);
      }else if (flag == 4) {
        long start = Long.parseLong(date + "01");
        long end = Long.parseLong(date + "12");
        return Pair.of(start, end);
      }
    } else {
      if (flag == 1) {
        long start = Long.parseLong(StringUtils.replace(LocalDate.now().toString(), "-", "") + "00");
        long end = Long.parseLong(StringUtils.replace(LocalDate.now().toString(), "-", "") + "24");
        return Pair.of(start, end);
      } else if (flag == 2) {
        long start = Long.parseLong(YearMonth.now().getYear() + "" + YearMonth.now().getMonthValue() + "00");
        long end = Long.parseLong(YearMonth.now().getYear() + "" + YearMonth.now().getMonthValue() + "32");
        return Pair.of(start, end);
      } else if (flag == 3) {
        long start = Long.parseLong(Year.now().getValue() + "01");
        long end = Long.parseLong(Year.now().getValue() + "12");
        return Pair.of(start, end);
      }else if (flag == 4) {
        long start = Long.parseLong(Year.now().getValue() + "01");
        long end = Long.parseLong(Year.now().getValue() + "12");
        return Pair.of(start, end);
      }
    }
    return Pair.of(-1L, -1L);
  }

  //date  eg. flag :1 pattern yyyy-MM-dd
  //date  eg. flag :2 pattern yyyy-MM
  //date  eg. flag :3 pattern yyyy
  public Long getBeforeDayOrMonthOrYear(String date, int flag) {
    if (StringUtils.isBlank(date)) {
      if (flag == 1) {
        String dateStr = LocalDate.now().minusDays(1).toString();
        String longDate = StringUtils.replace(dateStr, "-", "");
        return Long.parseLong(longDate);
      } else if (flag == 2) {
        String dateStr = LocalDate.now().minusMonths(1).toString();
        String beforeMonthStr2 =DateTimeUtils.date2String( DateTimeUtils.parseDate(dateStr,DateTimeUtils.FORMAT_yyyy_MM_dd),DateTimeUtils.FORMAT_yyyy_MM);
        String monthStr = StringUtils.substringBeforeLast(beforeMonthStr2, "-");
        String longDate = StringUtils.replace(monthStr, "-", "");
        return Long.parseLong(longDate);
      } else if (flag == 3) {
        return Long.parseLong(String.valueOf(Year.now().getValue() - 1));
      }
    } else {
      if (flag == 1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String beforeDayStr = LocalDate.parse(date, formatter).minusDays(1).toString();
        String longDate = StringUtils.replace(beforeDayStr, "-", "");
        return Long.parseLong(longDate);
      } else if (flag == 2) {
        String newDate = date + "-06";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String beforeMonthStr = LocalDate.parse(newDate, formatter).minusMonths(1).toString();
        String beforeMonthStr2 =DateTimeUtils.date2String( DateTimeUtils.parseDate(beforeMonthStr,DateTimeUtils.FORMAT_yyyy_MM_dd),DateTimeUtils.FORMAT_yyyy_MM);
        String longDate = StringUtils.replace(beforeMonthStr2, "-", "");
        return Long.parseLong(longDate);
      } else if (flag == 3) {
        return Long.parseLong(date) - 1;
      }
    }
    return -1L;
  }

}
