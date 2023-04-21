package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.ArithUtil;
import com.shmet.Consts;
import com.shmet.utils.GlobalConfig;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import com.shmet.vo.PhotovoltaicIncomeVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author
 */
@Service
public class StoreEnergyIncomeService {

  public static final Logger log = LoggerFactory.getLogger(StoreEnergyIncomeService.class);

  @Resource
  private DeviceMapper deviceMapper;

  @Resource
  private MongoTemplate mongoTemplate;

  @Autowired
  private CommonService commonService;

  //投资收益 --> 光电 风电收益 上方的 发电量 和 当前收益
  //dateType 1 按天 2 按月 3 按年
  //deviceModelType 2 代表 光电 3 代表风电
  public Map<String, Double> calcEpeAndIncome(String date, int dateType, List<Long> subprojectIds, int deviceModelType) {
    String sdate = commonService.transferDate(date, dateType);
    List<Long> deviceNos = commonService.getDeviceNos(subprojectIds, deviceModelType, 1);
    Map<String, Double> map = new LinkedHashMap<>();

    AggregationResults<Document> aggRes = null;
    //按天
    if (dateType == 1) {
      long start = Long.parseLong(sdate + "00");
      long end = Long.parseLong(sdate + "24");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(start).lte(end)),
          Aggregation.project().andExclude("_id")
              .and(StringOperators.Substr.valueOf("hour").substring(0, 8)).as("hour")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPE.price").as("price"),
          Aggregation.group("hour").sum("epediffsum").as("epesum").sum("price").as("epeprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    }
    //按月
    if (dateType == 2) {
      assert sdate != null;
      //sdate 202102
      long month = Long.parseLong(sdate);
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(month)),
          Aggregation.project("yearMonth").andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("price")
              .and("$statistics.EPI.price").as("price2"),
          Aggregation.group("yearMonth")
              .sum("epediffsum").as("epesum")
              .sum("epidiffsum").as("episum")
              .sum("price").as("epeprice")
              .sum("price2").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    //按年
    if (dateType == 3) {
      long start = Long.parseLong(StringUtils.substring(sdate, 0, 4) + "01");
      long end = Long.parseLong(StringUtils.substring(sdate, 0, 4) + "12");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(start).lte(end)),
          Aggregation.project("yearMonth").andExclude("_id")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPI.diffsum").as("epidiffsum")
              .and("$statistics.EPE.price").as("price")
              .and("$statistics.EPI.price").as("price2"),
          Aggregation.group("yearMonth")
              .sum("epediffsum").as("epesum")
              .sum("epidiffsum").as("episum")
              .sum("price").as("epeprice")
              .sum("price2").as("epiprice")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    double epesum = 0d;
    double epeprice = 0d;
    double episum = 0d;
    double epiprice = 0d;
    assert aggRes != null;
    for (Document doc : aggRes.getMappedResults()) {
      epesum = epesum + Double.parseDouble(String.valueOf(doc.get("epesum") == null ? 0d : doc.get("epesum")));
      episum = episum + Double.parseDouble(String.valueOf(doc.get("episum") == null ? 0d : doc.get("episum")));
      epeprice = epeprice + Double.parseDouble(String.valueOf(doc.get("epeprice") == null ? 0d : doc.get("epeprice")));
      epiprice = epiprice + Double.parseDouble(String.valueOf(doc.get("epiprice") == null ? 0d : doc.get("epiprice")));
    }

    map.put("epeprice", NumberUtil.convert2(ArithUtil.add(epiprice, epeprice)));
    map.put("epesum", NumberUtil.convert2(ArithUtil.add(episum, epesum)));
    map.put("episum", NumberUtil.convert2(episum));
    map.put("epiprice", NumberUtil.convert2(epiprice));
    return map;
  }

  public List<CalcDayCls> calcDay(String date, List<Long> subprojectIds, int flag) {
    List<CalcDayCls> res = new ArrayList<>();
    Pair<Long, Long> pair = dateToDayPair(date);
    List<Long> deviceNos = commonService.getDeviceNos(subprojectIds, flag, 1);

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
        Aggregation.project().andExclude("_id")
            .and(StringOperators.Substr.valueOf("$hour").substring(8, 2)).as("hour")
            .and("$statistics.EPI.diffsum").as("diffsumEPI")
            .and("$statistics.EPE.diffsum").as("diffsumEPE")
            .and("$statistics.EPI.price").as("epipricesum")
            .and("$statistics.EPE.price").as("epepricesum"),
        Aggregation.group("hour")
            .sum("$diffsumEPE").as("epesum")
            .sum("$diffsumEPI").as("episum")
            .sum("$epipricesum").as("epipricesum")
            .sum("$epepricesum").as("epepricesum"),
        Aggregation.sort(Sort.Direction.ASC, "_id")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    for (Document doc : aggRes.getMappedResults()) {
      String hour = doc.getString("_id");
      Double epesum = doc.getDouble("epesum");
      Double episum = doc.getDouble("episum");
      double epepricesum = Double.parseDouble(String.valueOf(doc.get("epepricesum") == null ? "0d" : doc.get("epepricesum")));
      double epipricesum = Double.parseDouble(String.valueOf(doc.get("epipricesum") == null ? "0d" : doc.get("epipricesum")));

      CalcDayCls cls = new CalcDayCls();
      cls.setHour(hour);
      cls.setEpesum(NumberUtil.convert2(epesum + episum));
      cls.setEpepricesum(NumberUtil.convert2(epepricesum + epipricesum));

      res.add(cls);
    }
    return res;
  }

  //date pattern eg. yyyy-MM-dd 按天查询
  public Map<String, Double> getEpeAndEpiByDay(String date, List<Long> subprojectIds) {
    Map<String, Double> map = new LinkedHashMap<>();
    Pair<Long, Long> pair = dateToDayPair(date);
    List<Long> deviceNos = getDeviceNos(subprojectIds);

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(pair.getLeft()).lte(pair.getRight())),
        Aggregation.project("hour", "deviceNo").andExclude("_id")
            .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE"),
        Aggregation.group("deviceNo").sum("$diffsumEPE").as("epesum").sum("$diffsumEPI").as("episum")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    for (Document doc : aggRes.getMappedResults()) {
      Double epesum = doc.getDouble("epesum");
      Double episum = doc.getDouble("episum");
      calcAdd(map, "epesum", epesum);
      calcAdd(map, "episum", episum);
    }
    Map<String, Double> beforeMap = getBeforeOneDayOrMonthOrYear(date, subprojectIds, 1);
    return getStringDoubleMap(map, beforeMap);
  }

  private Map<String, Double> getStringDoubleMap(Map<String, Double> map, Map<String, Double> beforeMap) {
    map.put("epesubval", NumberUtil.convert2(ArithUtil.sub(map.get("epesum") == null ? 0d : map.get("epesum"), beforeMap.get("beforeepesum") == null ? 0d : beforeMap.get("beforeepesum"))));
    map.put("episubval", NumberUtil.convert2(ArithUtil.sub(map.get("episum") == null ? 0d : map.get("episum"), beforeMap.get("beforeepisum") == null ? 0d : beforeMap.get("beforeepisum"))));
    return map;
  }

  //date pattern eg. yyyy-MM 按月查询
  public Map<String, Double> getEpeAndEpiByMonth(String date, List<Long> subprojectIds) {
    Map<String, Double> map = new LinkedHashMap<>();
    Long pair = dateToLong(date, "");
    List<Long> deviceNos = getDeviceNos(subprojectIds);

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(pair)),
        Aggregation.project("deviceNo").andExclude("_id")
            .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
    );
    aggExtractMethod(map, agg);
    Map<String, Double> beforeMap = getBeforeOneDayOrMonthOrYear(date, subprojectIds, 2);
    return getStringDoubleMap(map, beforeMap);
  }

  //date pattern eg. yyyy 按年查询
  public Map<String, Double> getEpeAndEpiByYear(String date, List<Long> subprojectIds) {
    Map<String, Double> map = new LinkedHashMap<>();
    long startTime = Long.parseLong(date + "01");
    long endTime = Long.parseLong(date + "12");
    List<Long> deviceNos = getDeviceNos(subprojectIds);

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(startTime).lte(endTime)),
        Aggregation.project("deviceNo").andExclude("_id")
            .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
    );
    aggExtractMethod(map, agg);
    Map<String, Double> beforeMap = getBeforeOneDayOrMonthOrYear(date, subprojectIds, 3);
    return getStringDoubleMap(map, beforeMap);
  }

  private void aggExtractMethod(Map<String, Double> map, Aggregation agg) {
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    for (Document doc : aggRes.getMappedResults()) {
      Double epesum = doc.getDouble("diffsumEPE");
      Double episum = doc.getDouble("diffsumEPI");
      calcAdd(map, "epesum", epesum);
      calcAdd(map, "episum", episum);
    }
  }

  //分组 统计当天各时段数据 date pattern eg. yyyy-MM-dd
  public JSONObject groupRealData(String date, Long subprojectId, int flag) {
    Pair<Long, Long> hourPair = commonService.getHourOrDefaultHour(date);
    List<Long> deviceNos = getDeviceNos(subprojectId, flag);

    MatchOperation currentMatch = Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(hourPair.getLeft()).lte(hourPair.getRight()));

    ProjectionOperation projectionOperation = Aggregation.project().andExclude("_id")
        .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
        .and("$statistics.EPI.price").as("epiPrice").and("$statistics.EPE.price").as("epePrice")
        .and("$statistics.EPE.priceType").as("priceType");
    GroupOperation groupOperation = Aggregation.group("priceType")
        .sum("epiPrice").as("epipricesum")
        .sum("epePrice").as("epepricesum")
        .sum("diffsumEPI").as("episum")
        .sum("diffsumEPE").as("epesum");
    Aggregation aggCurrent = Aggregation.newAggregation(
        currentMatch,
        projectionOperation,
        groupOperation
    );
    AggregationResults<Document> aggCurrentRes = mongoTemplate.aggregate(aggCurrent, "deviceRealData", Document.class);

    List<PhotovoltaicIncomeVo> currentIncomeVos = extractM1(aggCurrentRes);
    double epesum = currentIncomeVos.stream().map(PhotovoltaicIncomeVo::getEpesum).reduce(Double::sum).orElse(0d);
    //Double episum = currentIncomeVos.stream().map(PhotovoltaicIncomeVo::getEpisum).reduce(Double::sum).orElse(0d);
    Double income = currentIncomeVos.stream().map(PhotovoltaicIncomeVo::getEpepricesum).reduce(Double::sum).orElse(0d);
    //log.info("StoreEnergyIncomeService groupRealData currentIncomeVos size: {}", currentIncomeVos.size());

    JSONObject jsonObj = new JSONObject();
    jsonObj.put("items", currentIncomeVos);
    jsonObj.put("epesum", NumberUtil.convert2(Math.abs(epesum)));
    jsonObj.put("income", NumberUtil.convert2(income));
    return jsonObj;
  }

  //yyyy-MM
  public List<Triple<Long, Double, Double>> calcMonth(String month, List<Long> subprojectIds, int flag) {
    List<Triple<Long, Double, Double>> res = new ArrayList<>();
    long startTime, endTime;
    if (StringUtils.isNotBlank(month)) {
      startTime = Long.parseLong(StringUtils.replace(month, "-", "") + "00");
      endTime = Long.parseLong(StringUtils.replace(month, "-", "") + "32");
    } else {
      startTime = Long.parseLong(StringUtils.replace(StringUtils.substringBeforeLast(LocalDate.now().toString(), "-"), "-", "") + "00");
      endTime = Long.parseLong(StringUtils.replace(StringUtils.substringBeforeLast(LocalDate.now().toString(), "-"), "-", "") + "32");
    }
    List<Long> deviceNos = commonService.getDeviceNos(subprojectIds, flag, 1);
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(startTime).lte(endTime)),
        Aggregation.project("day").andExclude("_id")
            .and("$statistics.EPI.diffsum").as("episum")
            .and("$statistics.EPI.price").as("epiprice")
            .and("$statistics.EPE.diffsum").as("epesum")
            .and("$statistics.EPE.price").as("epeprice"),
        Aggregation.group("day")
            .sum("epesum").as("sum")
            .sum("episum").as("episum")
            .sum("epeprice").as("income")
            .sum("epiprice").as("epiprice")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(aggregation, "deviceDayStatistics", Document.class);
    return getTriples(res, aggRes);
  }

  //光电按年统计
  //yyyy
  public List<Triple<Long, Double, Double>> calcYear(String year, List<Long> subprojectIds, int flag) {
    List<Triple<Long, Double, Double>> res = new ArrayList<>();
    long startTime, endTime;
    if (StringUtils.isNotBlank(year)) {
      startTime = Long.parseLong(year + "00");
      endTime = Long.parseLong(year + "13");
    } else {
      startTime = Long.parseLong(Year.now().getValue() + "00");
      endTime = Long.parseLong(Year.now().getValue() + "13");
    }
    List<Long> deviceNos = commonService.getDeviceNos(subprojectIds, flag, 1);
    Aggregation aggregation = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(startTime).lte(endTime)),
        Aggregation.project("yearMonth").andExclude("_id")
            .and("$statistics.EPE.diffsum").as("epesum")
            .and("$statistics.EPI.diffsum").as("episum")
            .and("$statistics.EPE.price").as("epeprice")
            .and("$statistics.EPI.price").as("epiprice"),
        Aggregation.group("yearMonth")
            .sum("epesum").as("sum")
            .sum("episum").as("episum")
            .sum("epeprice").as("income")
            .sum("epiprice").as("epiprice")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(aggregation, "deviceMonthStatistics", Document.class);
    return getTriples(res, aggRes);
  }

  private List<Triple<Long, Double, Double>> getTriples(List<Triple<Long, Double, Double>> res, AggregationResults<Document> aggRes) {
    for (Document doc : aggRes) {
      Long month = doc.getLong("_id");
      double income = Double.parseDouble(String.valueOf(doc.get("income")));
      double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice")));
      double sum = Double.parseDouble(String.valueOf(doc.get("sum")));
      double episum = Double.parseDouble(String.valueOf(doc.get("episum")));

      Triple<Long, Double, Double> triple = Triple.of(month, NumberUtil.convert2(Math.abs(ArithUtil.add(sum, episum))), NumberUtil.convert2(ArithUtil.add(income, epiprice)));

      res.add(triple);
    }
    Collections.sort(res);
    return res;
  }

  public JSONObject calcElecSubVal(String date, Long subprojectId, int flag) {
    JSONObject res = new JSONObject();
    JSONObject currentJsonObj = groupRealData(date, subprojectId, flag);
    String beforeDay = strToLocalDate(date).minusDays(1).toString();
    JSONObject beforeJsonObj = groupRealData(beforeDay, subprojectId, flag);

    double epesubval = ArithUtil.sub(currentJsonObj.getDoubleValue("epesum"), beforeJsonObj.getDoubleValue("epesum"));
    double incomesubval = ArithUtil.sub(currentJsonObj.getDoubleValue("income"), beforeJsonObj.getDoubleValue("income"));
    res.put("epesubval", epesubval);
    res.put("incomesubval", incomesubval);

    Map<String, Double> currentMap = currentJsonObj.getJSONArray("items")
        .toJavaList(PhotovoltaicIncomeVo.class).stream().collect(toMap(PhotovoltaicIncomeVo::getPeriodName, PhotovoltaicIncomeVo::getEpesum, (o1, o2) -> o2));
    Map<String, Double> beforeMap = beforeJsonObj.getJSONArray("items")
        .toJavaList(PhotovoltaicIncomeVo.class).stream().collect(toMap(PhotovoltaicIncomeVo::getPeriodName, PhotovoltaicIncomeVo::getEpesum, (o1, o2) -> o2));
    calcMapSubVal(GlobalConfig.FENG, currentMap, beforeMap, res);
    calcMapSubVal(GlobalConfig.PING, currentMap, beforeMap, res);
    calcMapSubVal(GlobalConfig.GU, currentMap, beforeMap, res);
    //calcMapSubVal(GlobalConfig.JFENG, currentMap, beforeMap, res);
    return res;
  }

  private void calcMapSubVal(String str, Map<String, Double> currentMap, Map<String, Double> beforeMap, JSONObject res) {
    double currentVal = currentMap.get(str) == null ? 0d : currentMap.get(str);
    double beforeVal = beforeMap.get(str) == null ? 0d : beforeMap.get(str);
    double subVal = ArithUtil.sub(currentVal, beforeVal);
    res.put(str, NumberUtil.convert2(subVal));
  }

  private List<PhotovoltaicIncomeVo> extractM1(AggregationResults<Document> aggCurrentRes) {
    List<PhotovoltaicIncomeVo> incomeVos = new ArrayList<>();
    for (Document doc : aggCurrentRes.getMappedResults()) {
      PhotovoltaicIncomeVo incomeVo = new PhotovoltaicIncomeVo();

      Integer id = doc.getInteger("_id");
      double epesum = Double.parseDouble(String.valueOf(doc.get("epesum")));
      double episum = Double.parseDouble(String.valueOf(doc.get("episum")));
      double epipricesum = Double.parseDouble(String.valueOf(doc.get("epipricesum")));
      double epepricesum = Double.parseDouble(String.valueOf(doc.get("epepricesum")));

      if (id != null) {
        incomeVo.setPeriodName(numberToStr(id));
      } else {
        incomeVo.setPeriodName(numberToStr(1));
      }
      incomeVo.setEpesum(NumberUtil.convert2(ArithUtil.add(Math.abs(epesum), Math.abs(episum))));//epe+epi发电量
      incomeVo.setEpisum(NumberUtil.convert2(episum));
      incomeVo.setEpepricesum(NumberUtil.convert2(ArithUtil.add(Math.abs(epepricesum), Math.abs(epipricesum))));//epe+epi的发电费用
      incomeVo.setEpipricesum(NumberUtil.convert2(epipricesum));

      incomeVos.add(incomeVo);
    }

    return incomeVos;
  }

  private String numberToStr(Integer id) {
    if (id != null) {
      if (id == Consts.ELECTRIC_PRICE_PEAK) {
        return "Feng";
      } else if (id == Consts.ELECTRIC_PRICE_VALLEY) {
        return "Gu";
      } else if (id == Consts.ELECTRIC_PRICE_FLAT) {
        return "Ping";
      } else if (id == Consts.ELECTRIC_PRICE_AIGUILLE) {
        return "JFeng";
      }
    }
    return null;
  }

  public Map<String, Double> getBeforeOneDayOrMonthOrYear(String date, List<Long> subprojectIds, int flag) {
    Map<String, Double> map = new LinkedHashMap<>();
    List<Long> deviceNos = getDeviceNos(subprojectIds);

    AggregationResults<Document> aggRes = null;
    if (flag == 1) {
      Long triple = getBeforeOneDay(date);
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").is(triple)),
          Aggregation.project("deviceNo").andExclude("_id")
              .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    }
    if (flag == 2) {
      Long triple = getBeforeOneMonth(date);
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(triple)),
          Aggregation.project("deviceNo").andExclude("_id")
              .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    if (flag == 3) {
      Long triple = getBeforeOneYear(date);
      long startTime = Long.parseLong(triple + "01");
      long endTime = Long.parseLong(triple + "12");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(startTime).lte(endTime)),
          Aggregation.project("deviceNo").andExclude("_id")
              .and("$statistics.EPI.diffsum").as("diffsumEPI").and("$statistics.EPE.diffsum").as("diffsumEPE")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    if (aggRes != null) {
      List<Document> results = aggRes.getMappedResults();
      for (Document doc : results) {
        Double episum = doc.getDouble("diffsumEPI");
        Double epesum = doc.getDouble("diffsumEPE");
        calcAdd(map, "beforeepesum", epesum);
        calcAdd(map, "beforeepisum", episum);
      }
    }
    return map;
  }

  //pattern yyyy-MM-dd
  public Long dateToLong(String date, String s) {
    return Long.parseLong(date.replace("-", "") + s);
  }

  private Pair<Long, Long> dateToDayPair(String date) {
    if (StringUtils.isNotBlank(date)) {
      long startTime = dateToLong(date, "00");
      long endTime = dateToLong(date, "24");
      return Pair.of(startTime, endTime);
    } else {
      long startTime = dateToLong(LocalDate.now().toString(), "00");
      long endTime = dateToLong(LocalDate.now().toString(), "24");
      return Pair.of(startTime, endTime);
    }
  }

  public List<Long> getDeviceNos(Long subProjectId, int flag) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subProjectId)
        .eq("device_model", "1")
        .eq("device_model_type", flag))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  private List<Long> getDeviceNos(List<Long> subprojectIds) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model_type", 1)
        .eq("device_model", 1)
        .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  private void calcAdd(Map<String, Double> map, String key, Double d) {
    if (map.get(key) != null) {
      map.put(key, NumberUtil.convert2(ArithUtil.add(d, map.get(key))));
    } else {
      map.put(key, NumberUtil.convert2(d));
    }
  }

  //day pattern yyyyMMdd eg. 20201215 -->20201214
  public Long getBeforeOneDay(String date) {
    Long day;
    if (StringUtils.isBlank(date)) {
      day = dateToLong(LocalDate.now().minusDays(1).toString(), "");
    } else {
      LocalDate localDate = strToLocalDate(date);
      day = dateToLong(localDate.minusDays(1).toString(), "");
    }
    return day;
  }

  public LocalDate strToLocalDate(String str) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(str, formatter);
  }

  //month pattern yyyyMM eg. 202012 -->202011  202001-->201912
  public Long getBeforeOneMonth(String date) {
    Long month;
    if (StringUtils.isBlank(date)) {
      month = dateToLong(LocalDate.now().minusMonths(1).toString(), "");
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      month = dateToLong(YearMonth.parse(date, formatter).minusMonths(1).toString(), "");
    }
    return month;
  }

  //year pattern yyyy eg. 2020 --> 2019
  public Long getBeforeOneYear(String date) {
    Long year;
    if (StringUtils.isBlank(date)) {
      year = dateToLong(LocalDate.now().minusYears(1).toString(), "");
    } else {
      year = Long.parseLong(date) - 1;
    }
    return year;
  }

  private static class CalcDayCls {
    private String hour;
    private Double epesum;
    private Double epepricesum;

    public String getHour() {
      return hour;
    }

    public void setHour(String hour) {
      this.hour = hour;
    }

    public Double getEpesum() {
      return epesum;
    }

    public void setEpesum(Double epesum) {
      this.epesum = epesum;
    }

    public Double getEpepricesum() {
      return epepricesum;
    }

    public void setEpepricesum(Double epepricesum) {
      this.epepricesum = epepricesum;
    }
  }


}
