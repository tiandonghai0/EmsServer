package com.shmet.handler.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.shmet.ArithUtil;
import com.shmet.helper.redis.RedisUtil;
import com.shmet.utils.DateUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mongo.DeviceMonthStatistics;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class RealLoadService {
  //~~~~~~~~~~~static fileds start~~~~~~~~~~~~~
  private static final Logger log = LoggerFactory.getLogger(RealLoadService.class);
  //~~~~~~~~~~~~~static fileds end~~~~~~~~~~~

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private ThreadPoolExecutor executor;

  @Resource
  private MongoTemplate mongoTemplate;

  @Autowired
  private SubProjectService subProjectService;

  @Autowired
  private CommonService commonService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StoreEnergyIncomeService storeEnergyIncomeService;

  @Autowired
  private RedisUtil redisUtil;

  //device_model_type : 0 市点电能表、1 储能电能表，2 光电电能表，3 风电电能表
  public List<Long> getDeviceNos(List<Long> subProjectIds, int flag) {
    if (subProjectIds != null && subProjectIds.size() > 0) {
      return deviceMapper.selectList(new QueryWrapper<Device>()
          .eq("device_model_type", flag)
          .eq("device_model", 1)
          .eq("sub_project_id", subProjectIds.get(0)).orderByAsc("device_id"))
          .stream().map(Device::getDeviceNo).collect(toList());
    }
    return Lists.newArrayList();
  }


  //device_model_type : 0 市点电能表、1 储能电能表，2 光电电能表，3 风电电能表
  public List<Long> getDeviceNos(List<Long> subProjectIds, int deviceModel, int deviceModelType) {
    if (subProjectIds != null && subProjectIds.size() > 0) {
      return deviceMapper.selectList(new QueryWrapper<Device>()
          .eq("device_model_type", deviceModelType)
          .eq("device_model", deviceModel)
          .in("sub_project_id", subProjectIds))
          .stream().map(Device::getDeviceNo).collect(toList());
    }
    return Lists.newArrayList();
  }

  //首页大屏展示数据(合并过了)
  public JSONObject realLoadDataFromMongo(String date, List<Long> subProjectIds, int flag) {
    return mapToJson(queryMongoOriginData(date, subProjectIds, flag));
  }

  /**
   * 查询mongo中的原始数据
   */
  private Map<Long, List<Document>> queryMongoOriginData(String date, List<Long> subProjectIds, int flag) {
    Pair<Long, Long> timePair = DateUtils.convertDateToPair(date);
    MatchOperation hourMatch = Aggregation.match(Criteria.where("hour").lte(timePair.getRight()).gte(timePair.getLeft()));
    Aggregation aggH = groupAgg2(flag, hourMatch, subProjectIds);
    AggregationResults<Document> aggResH = mongoTemplate.aggregate(aggH, "deviceRealData", Document.class);
    List<Document> docs = (List<Document>) aggResH.iterator().next().get("result");
    return docs.stream().collect(Collectors.groupingBy(o -> groupByDeviceNo(o.getLong("deviceNo"))));
  }

  /**
   * 运行调度 储能电表功率数据 (未分组)
   */
  public JSONArray realLoadStoreEnergyData(String date, List<Long> subProjectIds, int flag) {
    JSONArray jsonArray = new JSONArray();
    Map<Long, List<Document>> map = queryMongoOriginData(date, subProjectIds, flag);
    for (Map.Entry<Long, List<Document>> entry : map.entrySet()) {
      List<String> timeList = new ArrayList<>();
      List<Double> varList = new ArrayList<>();
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("deviceNo", entry.getKey());
      for (Document doc : entry.getValue()) {
        timeList.add(String.valueOf(doc.getLong("timestamp")).substring(8));
        varList.add(doc.getDouble("v"));
      }
      jsonObj.put("var", varList);
      jsonObj.put("time", timeList);
      Double realload = varList.get(varList.size() - 1);
      jsonObj.put("realload", realload);
      jsonArray.add(jsonObj);
    }
    return jsonArray;
  }

  /**
   * 运行调度 电池数据 (未分组) 查询电池深度SOC 单体最高电压(HCV) 单体最低电压(LCV)
   */
  public Map<Long, Map<String, Collection<Double>>> realLoadBmsData(String date, List<Long> subprojectIds) {
    Pair<Long, Long> timepair = DateUtils.convertDateToPair(date);
    //List<Double> totalCapacity = subProjectService.getTotalCapacitys(subprojectIds);
    List<Long> deviceNos = commonService.photoVoltaicNos(subprojectIds, 28, null);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(timepair.getLeft()).lte(timepair.getRight())),
        Aggregation.project().andExclude("statistics").andExclude("_id"),
        Aggregation.unwind("metrics"),
        Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp").and((ObjectOperators.valueOf("$metrics.datas").toArray())).as("datas"),
        Aggregation.unwind("datas"),
        Aggregation.match(Criteria.where("datas.k").in("SOC", "HCV", "LCV")),
        Aggregation.project().and("$deviceNo").as("deviceNo").and("$timestamp").as("timestamp").and("$datas.v").as("v"),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo", "timestamp"),
        Aggregation.facet(Aggregation.limit(100000)).as("result")
            .and(Aggregation.count().as("count")).as("totalCount")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    List<Document> docs = (List<Document>) aggRes.iterator().next().get("result");
    return docToMap(docs);
  }

  /**
   * 计算每天的(最高用电量最低用电量)以及对应的时间
   *
   * @param date          天 格式必须是 yyyy-MM-dd 不传默认是 当天日期
   * @param subprojectIds subprojectIds
   * @return JSONArray
   */
  public JSONArray calc(String date, List<Long> subprojectIds) {
    Pair<Long, Long> timepair = DateUtils.convertDateToPair(date);
    List<Long> deviceNos = commonService.photoVoltaicNos(subprojectIds, 28, 1);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(timepair.getLeft()).lte(timepair.getRight())),
        Aggregation.project().andExclude("statistics").andExclude("_id"),
        Aggregation.unwind("metrics"),
        Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp").and((ObjectOperators.valueOf("$metrics.datas").toArray())).as("datas"),
        Aggregation.unwind("datas"),
        Aggregation.match(Criteria.where("datas.k").in("SOC")),
        Aggregation.project().and("$deviceNo").as("deviceNo").and("$timestamp").as("timestamp").and("$datas.v").as("v"),
        Aggregation.group("deviceNo").max("v").as("maxV").min("v").as("minV").first("timestamp").as("timeMin").last("timestamp").as("timeMax")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    Map<Long, Document> map = aggRes.getMappedResults().stream().collect(Collectors.toMap(d -> d.getLong("_id"), o -> o));
    JSONArray jsonArray = new JSONArray();
    List<Double> totalCapacitys = subProjectService.getTotalCapacitys(subprojectIds);
    for (Map.Entry<Long, Document> entry : map.entrySet()) {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("deviceNo", entry.getKey());
      Document doc = entry.getValue();
      jsonObj.put("maxV", doc.getDouble("maxV"));
      jsonObj.put("minV", doc.getDouble("minV"));
      jsonObj.put("maxElec", NumberUtil.convert2(doc.getDouble("maxV") * totalCapacitys.get(0)));
      jsonObj.put("minElec", NumberUtil.convert2(doc.getDouble("minV") * totalCapacitys.get(0)));
      jsonObj.put("timeMin", DateUtils.longConvertDateStr(doc.getLong("timeMin")));
      jsonObj.put("timeMax", DateUtils.longConvertDateStr(doc.getLong("timeMax")));
      jsonArray.add(jsonObj);
    }

    return jsonArray;
  }

  /**
   * 运行调度 SOC HCV LCV 值
   *
   * @param docs docs
   * @return Map<Long, Map < String, Collection < Double>>>
   */
  private Map<Long, Map<String, Collection<Double>>> docToMap(List<Document> docs) {
    Map<Long, Map<String, Collection<Double>>> resMap = new LinkedHashMap<>();
    Set<Long> deviceNos = docs.parallelStream().map(o -> o.getLong("deviceNo")).collect(Collectors.toSet());
    deviceNos.forEach(dno -> {
      Multimap<String, Double> mmap = LinkedHashMultimap.create();
      docs.forEach(d -> {
        if (dno.equals(d.getLong("deviceNo"))) {
          String timestamp = StringUtils.substring(d.getLong("timestamp").toString(), 8);
          Double v = d.getDouble("v");
          mmap.put(timestamp, v);
        }
      });
      resMap.put(dno, mmap.asMap());
    });
    return resMap;
  }

  /**
   * 首页大屏展示 需要合并多个deviceNo的数据
   *
   * @param map map
   * @return JSONObject
   */
  private JSONObject mapToJson(Map<Long, List<Document>> map) {
    JSONObject jsonObj = new JSONObject();
    Map<String, Double> resMap = new LinkedHashMap<>();
    for (Map.Entry<Long, List<Document>> entry : map.entrySet()) {
      for (Document doc : entry.getValue()) {
        String timestamp = String.valueOf(doc.getLong("timestamp")).substring(8);
        Double v = doc.getDouble("v");
        if (resMap.get(timestamp) != null) {
          resMap.put(timestamp, ArithUtil.add(v, resMap.get(timestamp)));
        } else {
          resMap.put(timestamp, v);
        }
      }
    }
    jsonObj.put("time", resMap.keySet());
    List<Double> varlist = new ArrayList<>(resMap.values());
    jsonObj.put("var", varlist);
    if (varlist.size() > 0) {
      jsonObj.put("realload", varlist.get(varlist.size() - 1));
    }
    return jsonObj;
  }

  /**
   * 计算年月日最高功率最低功率 最高功率对应的时间 最低功率对应的时间
   */
  public JSONObject caclMaxAndMinVal(String date, List<Long> subProjectIds, int flag) throws ExecutionException, InterruptedException {
    Pair<Long, Long> timePair = DateUtils.convertDateToPair(date);
    //eg. 2020110000 - 2020113200
    long startMonth = Long.parseLong(StringUtils.substring(timePair.getLeft().toString(), 0, 6) + "0000");
    long endMonth = Long.parseLong(StringUtils.substring(timePair.getLeft().toString(), 0, 6) + "3200");
    //eg. 2020000000 - 2020120000
    long startYear = Long.parseLong(StringUtils.substring(timePair.getLeft().toString(), 0, 4) + "000000");
    long endYear = Long.parseLong(StringUtils.substring(timePair.getLeft().toString(), 0, 4) + "120000");
    MatchOperation hourMatch = Aggregation.match(Criteria.where("hour").lte(timePair.getRight()).gte(timePair.getLeft()));
    MatchOperation monthMatch = Aggregation.match(Criteria.where("hour").lte(endMonth).gte(startMonth));
    MatchOperation yearMatch = Aggregation.match(Criteria.where("hour").lte(endYear).gte(startYear));
    Aggregation aggH = groupAgg(flag, hourMatch, subProjectIds);
    Aggregation aggM = groupAgg(flag, monthMatch, subProjectIds);
    Aggregation aggY = groupAgg(flag, yearMatch, subProjectIds);
    //AggregationResults<Document> aggResY = executor.submit(() -> mongoTemplate.aggregate(aggY, "deviceRealData", Document.class)).get();
    AggregationResults<Document> aggResM = executor.submit(() -> mongoTemplate.aggregate(aggM, "deviceRealData", Document.class)).get();
    AggregationResults<Document> aggResD = executor.submit(() -> mongoTemplate.aggregate(aggH, "deviceRealData", Document.class)).get();
    List<Document> docs = (List<Document>) aggResD.iterator().next().get("result");
    List<Document> docsM = (List<Document>) aggResM.iterator().next().get("result");
    //List<Document> docsY = (List<Document>) aggResY.iterator().next().get("result");
    JSONObject jsonObj = new JSONObject();
    //************************************************************************
    Pair<Double, Double> dpair = calcMaxPAndMinP(docs);
    Pair<Double, Double> mpair = calcMaxPAndMinP(docsM);
    //Pair<Double, Double> ypair = calcMaxPAndMinP(docsY);
    Pair<String, String> dtimepair = CompletableFuture.supplyAsync(() -> calcMaxTimeAndMinTime(docs, dpair.getLeft(), dpair.getRight()), executor).join();
    Pair<String, String> mtimepair = CompletableFuture.supplyAsync(() -> calcMaxTimeAndMinTime(docsM, mpair.getLeft(), mpair.getRight()), executor).join();
    //Pair<String, String> ytimepair = CompletableFuture.supplyAsync(() -> calcMaxTimeAndMinTime(docsY, ypair.getLeft(), ypair.getRight()), executor).join();
    jsonObj.put("dMinP", dpair.getRight());
    jsonObj.put("dMinPTime", dtimepair.getRight());
    jsonObj.put("dMaxP", dpair.getLeft());
    jsonObj.put("dMaxPTime", dtimepair.getLeft());
    jsonObj.put("mMinP", mpair.getRight());
    jsonObj.put("mMinPTime", mtimepair.getRight());
    jsonObj.put("mMaxPTime", mtimepair.getLeft());
    jsonObj.put("mMaxP", mpair.getLeft());
    return jsonObj;
  }

  private static Long groupByDeviceNo(Long deviceNo) {
    return deviceNo;
  }

  /**
   * 抽取aggregation
   *
   * @param flag          1: 日 2: 月 3: 年
   * @param match         匹配条件
   * @param subProjectIds subprojectIds
   * @return combine Aggreation
   */
  private Aggregation groupAgg(int flag, AggregationOperation match, List<Long> subProjectIds) {
    List<Long> deviceNos;
    MatchOperation pmatch;
    if (flag == 27) {
      deviceNos = getDeviceNos(subProjectIds, 27, 0);
      pmatch = Aggregation.match(Criteria.where("datas.k").is("AP"));
    } else {
      deviceNos = getDeviceNos(subProjectIds, flag);
      pmatch = Aggregation.match(Criteria.where("datas.k").is("P"));
    }
    log.info("RealLoadService groupAgg deviceNos: {}", deviceNos);
    return Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos)),
        match,
        Aggregation.project().andExclude("statistics").andExclude("_id"),
        Aggregation.unwind("metrics"),
        Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp").and("metrics.datas").as("datas"),
        Aggregation.project("deviceNo").and("timestamp").as("timestamp").and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
        Aggregation.unwind("datas"),
        pmatch,
        Aggregation.project().and("$deviceNo").as("deviceNo").and("$timestamp").as("timestamp").and("$datas.v").as("v"),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo", "timestamp"),
        Aggregation.facet(Aggregation.limit(100000)).as("result")
            .and(Aggregation.count().as("count")).as("totalCount")
    );
  }
  private Aggregation groupAgg2(int flag, AggregationOperation match, List<Long> subProjectIds) {
    MatchOperation pmatch;

    Long deviceNos = getDeviceNos(subProjectIds, flag).get(0);
      pmatch = Aggregation.match(Criteria.where("datas.k").is("P"));

    log.info("RealLoadService groupAgg deviceNos: {}", deviceNos);
    return Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").is(deviceNos)),
            match,
            Aggregation.project().andExclude("statistics").andExclude("_id"),
            Aggregation.unwind("metrics"),
            Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp").and("metrics.datas").as("datas"),
            Aggregation.project("deviceNo").and("timestamp").as("timestamp").and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
            Aggregation.unwind("datas"),
            pmatch,
            Aggregation.project().and("$deviceNo").as("deviceNo").and("$timestamp").as("timestamp").and("$datas.v").as("v"),
            Aggregation.sort(Sort.Direction.ASC, "deviceNo", "timestamp"),
            Aggregation.facet(Aggregation.limit(100000)).as("result")
                    .and(Aggregation.count().as("count")).as("totalCount")
    );
  }
  /**
   * 计算最大最小值
   *
   * @param docs docs
   * @return Pair
   */
  private Pair<Double, Double> calcMaxPAndMinP(List<Document> docs) {
    Double maxV = docs.parallelStream().map(o -> o.getDouble("v")).max(Comparator.comparingDouble(o -> o)).orElse(0d);
    Double minV = docs.parallelStream().map(o -> o.getDouble("v")).min(Comparator.comparingDouble(o -> o)).orElse(0d);
    return Pair.of(maxV, minV);
  }

  /**
   * 计算最大最小值对应 的时间 若存在多个则只取第一个
   *
   * @param docs docs
   * @return Pair
   */
  public Pair<String, String> calcMaxTimeAndMinTime(List<Document> docs, Double maxV, Double minV) {
    Long maxVTime = docs.parallelStream().filter(o -> maxV.equals(o.getDouble("v"))).findFirst().orElse(new Document()).getLong("timestamp");
    Long minVTime = docs.parallelStream().filter(o -> minV.equals(o.getDouble("v"))).findFirst().orElse(new Document()).getLong("timestamp");
    String maxTStr = DateUtils.longConvertDateStr(maxVTime);
    String minTStr = DateUtils.longConvertDateStr(minVTime);
    return Pair.of(maxTStr, minTStr);
  }

  //华银计算功率P
  public Map<String, Object> calcHuayinP() {
    Map<String, Object> map = new LinkedHashMap<>();
    List<String> shineDeviceNos = Lists.newArrayList("202040010010003", "202040010010004", "202040010010005", "202040010010006", "202040010010007", "202040010010008",
        "202040010010009", "202040010010010", "202040010010011", "202040010010012", "202040010010013", "202040010010014", "202040010010015");
    //List<String> itemStrs = commonService.cursorItems(shineDeviceNos, "P");
    List<String> itemStrs = new ArrayList<>(16);
    for (String dno : shineDeviceNos) {
      String s = (String) redisUtil.hget("RealDataRedisDao.saveRealDataCache", dno + ".P", -1);
      itemStrs.add(s);
    }
    List<RealDataItem> items = new ArrayList<>();
    for (String item : itemStrs) {
      if (item != null) {
        RealDataItem realData = JSON.parseObject(item, RealDataItem.class);
        items.add(realData);
      }
    }
    map.put("items", items);
    return map;
  }

  //查询 华银 储能 光电 风电 总发电量 充电量
  public List<Map<String, Double>> queryHuayinElec() {
    List<Map<String, Double>> list = new ArrayList<>();
    //储能收益
    Map<String, Double> storeMap = calcStoreMap();
    //风电收益
    Query query2 = new Query(Criteria.where("deviceNo").is(202040010010003L).and("yearMonth").gte(202001).lte(202212));
    Map<String, Double> windMap = calcEx(query2, Lists.newArrayList(), Lists.newArrayList(), "windSum", "windIncomeSum");
    //光伏收益
    List<Long> shineDeviceNos = Lists.newArrayList(202040010010005L, 202040010010007L, 202040010010008L,
        202040010010009L, 202040010010010L, 202040010010011L, 202040010010012L, 202040010010013L, 202040010010014L);
    Query query3 = new Query(Criteria.where("deviceNo").in(shineDeviceNos).and("yearMonth").gte(202001).lte(202212));
    Map<String, Double> shineMap = calcEx(query3, Lists.newArrayList(), Lists.newArrayList(), "shineSum", "shineIncomeSum");
    list.add(storeMap);
    list.add(windMap);
    list.add(shineMap);
    return list;
  }

  public Map<String, Double> calcEx(Query query, List<Double> sums, List<Double> incomes, String sumType, String incomeType) {
    Map<String, Double> map = new LinkedHashMap<>();
    //计算当天的收益 跟 总发电量
    JSONObject jsonObj = storeEnergyIncomeService.groupRealData(LocalDate.now().toString(), 20202002001L, 2);
    List<DeviceMonthStatistics> statistics = mongoTemplate.find(query, DeviceMonthStatistics.class);
    List<Double> episums = new ArrayList<>();
    for (DeviceMonthStatistics statistic : statistics) {
      Double epesum = Double.parseDouble(String.valueOf(statistic.getStatistics().get("EPE").get("diffsum") == null ? 0d : statistic.getStatistics().get("EPE").get("diffsum")));
      double epeIncome = Double.parseDouble(String.valueOf(statistic.getStatistics().get("EPE").get("price") == null ? 0d : statistic.getStatistics().get("EPE").get("price")));
      double epiIncome = Double.parseDouble(String.valueOf(statistic.getStatistics().get("EPI").get("price") == null ? 0d : statistic.getStatistics().get("EPI").get("price")));
      if (StringUtils.equals("shineSum", sumType)) {
        Double episum = Double.parseDouble(String.valueOf(statistic.getStatistics().get("EPI").get("diffsum") == null ? 0d : statistic.getStatistics().get("EPI").get("diffsum")));
        episums.add(episum);
      }
      sums.add(epesum);
      incomes.add(ArithUtil.add(epeIncome, epiIncome));
    }
    Double sumDouble = sums.stream().reduce(Double::sum).orElse(0d);
    Double incomeDouble = incomes.stream().reduce(Double::sum).orElse(0d);
    Double episumDouble = episums.stream().reduce(Double::sum).orElse(0d);
    if (StringUtils.equals("shineSum", sumType)) {
      Double daySum = jsonObj.getDouble("epesum");
      map.put(sumType, ArithUtil.add(daySum, ArithUtil.add(sumDouble, episumDouble)));
      //map.put(incomeType, ArithUtil.add(incomeDouble, jsonObj.getDouble("income")));
    } else {
      map.put(sumType, sumDouble);
    }
    map.put(incomeType, incomeDouble);
    return map;
  }

  public Map<String, Double> calcStoreMap() {
    Map<String, Double> map = new LinkedHashMap<>();
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").is(202040010010015L).and("hour").gte(2020010100L).lte(2030010100L)),
        Aggregation.project().andExclude("_id")
            .and("$statistics.EPE.diffsum").as("epesum")
            .and("$statistics.EPI.diffsum").as("episum")
            .and("$statistics.EPE.price").as("epeprice")
            .and("$statistics.EPI.price").as("epiprice")

    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);

    List<Double> epesumLst = new ArrayList<>();
    List<Double> epepriceLst = new ArrayList<>();
    List<Double> episumLst = new ArrayList<>();
    List<Double> epipriceLst = new ArrayList<>();

    for (Document doc : aggRes) {
      double epesum = Double.parseDouble(String.valueOf(doc.get("epesum")));
      double episum = Double.parseDouble(String.valueOf(doc.get("episum")));
      double epeprice = Double.parseDouble(String.valueOf(doc.get("epeprice")));
      double epiprice = Double.parseDouble(String.valueOf(doc.get("epiprice")));

      epesumLst.add(epesum);
      epepriceLst.add(epeprice);
      episumLst.add(episum);
      epipriceLst.add(epiprice);
    }

    Double epesumDouble = epesumLst.stream().reduce(Double::sum).orElse(0d);
    Double episumDouble = episumLst.stream().reduce(Double::sum).orElse(0d);
    Double epepriceDouble = epepriceLst.stream().reduce(Double::sum).orElse(0d);
    Double epipriceDouble = epipriceLst.stream().reduce(Double::sum).orElse(0d);

    map.put("storeSum", epesumDouble);
    map.put("episum", episumDouble);
    map.put("storeIncomeSum", ArithUtil.sub(epipriceDouble, epepriceDouble));

    return map;
  }

  /**
   * calc realdata
   *
   * @param subprojectIds subprojectIds
   * @param deviceModel   deviceModel
   * @return List<Map < String, Object>>
   */
  //public Map<String, List<Pair<String, String>>> queryStoreReal(List<Long> subprojectIds, Integer deviceModel) {
  public List<RealDataVo> queryStoreReal(List<Long> subprojectIds, Integer deviceModel) {
    List<Map<String, List<?>>> res = Lists.newArrayList();

    List<Long> deviceNos = commonService.getDeviceNos(subprojectIds, deviceModel);
    List<String> strs = commonService.cursorItems(deviceNos);

    List<RealDataItem> items = Lists.newArrayList();

    for (String s : strs) {
      try {
        RealDataItem data = objectMapper.readValue(s, RealDataItem.class);
        items.add(data);
      } catch (IOException ignored) {
      }
    }

    Map<String, List<RealDataItem>> listMap = items.stream().collect(groupingBy(RealDataItem::getDeviceNo));

    //return toMapPair(listMap);
    return toBeanPair(listMap);
  }

  private Map<String, List<Pair<String, String>>> toMapPair(Map<String, List<RealDataItem>> map) {
    Map<String, List<Pair<String, String>>> res = new HashMap<>();
    for (Map.Entry<String, List<RealDataItem>> entry : map.entrySet()) {
      List<Pair<String, String>> pairs = new ArrayList<>();
      for (RealDataItem dataItem : entry.getValue()) {
        Pair<String, String> p = Pair.of(dataItem.getTagCode(), getStringValue(dataItem.getData()));
        pairs.add(p);
      }

      res.put(entry.getKey(), pairs);
    }
    return res;
  }

  private List<RealDataVo> toBeanPair(Map<String, List<RealDataItem>> map) {
    List<RealDataVo> vos = new ArrayList<>();

    for (Map.Entry<String, List<RealDataItem>> entry : map.entrySet()) {
      RealDataVo vo = new RealDataVo();

      List<RealDataItem> values = entry.getValue();
      vo.setDeviceNo(entry.getKey());
      vo.setTime(values.get(0).getDateTime());

      Map<String, String> m = new HashMap<>();

      for (RealDataItem dataItem : values) {
        m.put(dataItem.getTagCode(), getStringValue(dataItem.getData()));
        vo.setDataMap(m);
      }

      vos.add(vo);
    }
    return vos;
  }

  @Getter
  @Setter
  static class RealDataVo {
    private String time;
    private String deviceNo;
    private Map<String, String> dataMap;
  }

  private String getStringValue(Object o) {
    if (o instanceof List) {
      List<?> lst = (List<?>) o;
      if (lst.get(1) instanceof List) {
        return getStringValue(lst.get(1));
      } else {
        return String.valueOf(lst.get(0));
      }

    } else {
      return String.valueOf(o);
    }
  }
}
