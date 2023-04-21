package com.shmet.handler.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.*;
import com.shmet.dao.ChargeDataByMonthMapper;
import com.shmet.dao.ChargeDataByYearMapper;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.BaseChargeData;
import com.shmet.entity.mysql.gen.ChargeDataByMonth;
import com.shmet.entity.mysql.gen.ChargeDataByYear;
import com.shmet.entity.mysql.gen.Device;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * @author
 */
@Service
public class ElectricCruveLineService {

  public static final Logger logger = LoggerFactory.getLogger(ElectricCruveLineService.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Resource
  private DeviceMapper deviceMapper;

  @Resource
  private ChargeDataByMonthMapper monthMapper;

  @Resource
  private ChargeDataByYearMapper yearMapper;

  /**
   * 根据 subProjectId 查询所有的 deviceNos  所有市电电表的deviceNos
   *
   * @param subProjectId subProjectId
   * @param flag         0：代表市电 1：代表储能电表
   * @return deviceNos
   */
  public List<Long> getDeviceNos(Long subProjectId, int flag) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subProjectId)
        .eq("device_model", "1")
        .eq("device_model_type", flag))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 按天分组数据 格式 for example:
   * 20201001:[
   * {"nosummerFeng":16.0},
   * {"nosummerPing":17.6},
   * {"nosummerGu":13.6},
   * ],
   * 20201002:[
   * {"nosummerFeng":21.6},
   * {"nosummerPing":23.2},
   * {"nosummerGu":12.8},
   * ]
   * ...
   *
   * @param date         格式必须是 yyyy/MM
   * @param subProjectId subProjectId
   * @return Multimap<Integer, Map < String, Double>>
   */
  public Multimap<Integer, Map<String, Double>> mergeGroupDataByDay(String date, Long subProjectId, int flag) {
    List<Map<Integer, Map<String, Double>>> mapLists = getMonthDayHourData(date, subProjectId, flag);
    List<Integer> keys = new ArrayList<>(mapLists.get(mapLists.size() - 1).keySet());
    Multimap<Integer, Map<String, Double>> resMap = LinkedHashMultimap.create();
    for (Map<Integer, Map<String, Double>> ml : mapLists) {
      for (Integer key : keys) {
        Map<String, Double> map = ml.get(key);
        if (map != null) {
          resMap.put(key, map);
        }
      }
    }

    return resMap;
  }

  /**
   * 求得最原始的数据 一个月中每天每小时的数据
   *
   * @param date         时间格式 必须是yyyy/MM
   * @param subProjectId subProjectId
   */
  public List<Map<Integer, Map<String, Double>>> getMonthDayHourData(String date, Long subProjectId, int flag) {
    //根据subProjectId 查询 deviceNos
    List<Long> deviceNos = getDeviceNos(subProjectId, flag);
    //传入的时间格式为yyyy/MM 需要转换
    int month = Integer.parseInt(StringUtils.split(date, "/")[1]);
    String ymonth = StringUtils.replace(date, "/", "");
    //forExample startDate:2020090000  endDate:2020093200
    long startDate = Long.parseLong(ymonth + "0000");
    long endDate = Long.parseLong(ymonth + "3200");
    //用于获取当前是夏季还是非夏季 判断是否属于夏季月份
    boolean isSummerMonth = isSummerMonth(subProjectId, month);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(startDate).lte(endDate)),
        Aggregation.project().andExclude("_id").and(StringOperators.SubstrCP.valueOf("$hour").substringCP(0, 11)).as("h")
            .and("$statistics.EPI.diffsum").as("diffsum"),
        Aggregation.sort(Sort.Direction.ASC, "h")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    //得到 MM 月每天每小时的原始数据
    Map<Integer, Double> mapDatas = calcAggDataToMap(Maps.newLinkedHashMap(), aggRes);
    //将每天每小时的数据挂在 到 每一天下
    Multimap<Integer, Map<Integer, Double>> multimap = groupByDayWithHour(mapDatas);
    //得到这一个月的各个时段
    List<Map<String, List<Pair<Integer, Integer>>>> timePeriodMaps = splitTimePeriod(subProjectId, isSummerMonth);

    List<Map<Integer, Map<String, Double>>> maps = new ArrayList<>();

    //本月中各时段 for example: nosummerFeng nosummerPing nosummerGu
    for (Map<String, List<Pair<Integer, Integer>>> mp : timePeriodMaps) {
      Map<Integer, Map<String, Double>> resDayMap = new LinkedHashMap<>();
      //for example:nosummerFeng 10-15 18-21 ...
      for (Map.Entry<String, List<Pair<Integer, Integer>>> seasonEntry : mp.entrySet()) {
        String seasonKey = seasonEntry.getKey();
        //key: 当天时间 20201009 value: {key:当天小时,value:具体数据}
        for (Map.Entry<Integer, Map<Integer, Double>> entry : multimap.entries()) {
          Integer daykey = entry.getKey();
          Map<String, Double> tmpMap = new LinkedHashMap<>();
          //获取到具体的 k v
          for (Map.Entry<Integer, Double> doubleEntry : entry.getValue().entrySet()) {
            Double d = doubleEntry.getValue();
            Integer hour = doubleEntry.getKey();
            //判断是处于哪个时段
            for (Pair<Integer, Integer> pair : seasonEntry.getValue()) {
              if (hour >= pair.getLeft() && hour < pair.getRight()) {
                if (resDayMap.containsKey(daykey)) {
                  tmpMap.put(seasonKey, addDouble(d, resDayMap.get(daykey).get(seasonKey)));
                } else {
                  tmpMap.put(seasonKey, d);
                }
                resDayMap.put(daykey, tmpMap);
                break;
              }
            }
          }
        }
      }
      maps.add(resDayMap);
    }

    return maps;
  }


  /**
   * 当选择 月为单位时
   *
   * @param date         年月格式必须是 yyyy/mm
   * @param subProjectId sub项目编号
   * @return 原始集合数据
   */
  public Map<Integer, Double> getOriginDataByMonth(String date, Long subProjectId, int flag) {
    //根据subProjectId 查询 deviceNos
    List<Long> deviceNos = getDeviceNos(subProjectId, flag);
    //传入的时间格式为yyyy/MM 需要转换
    String monthDate = StringUtils.replace(date, "/", "");
    //for example 2020090000 2020093200
    long startDate = Long.parseLong(monthDate + "0000");
    long endDate = Long.parseLong(monthDate + "3200");

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(startDate).lte(endDate)),
        Aggregation.project().andExclude("_id").and(StringOperators.SubstrCP.valueOf("$hour").substringCP(8, 2)).as("h")
            .and("$statistics.EPI.diffsum").as("diffsum"),
        Aggregation.sort(Sort.Direction.ASC, "h")
    );
    AggregationResults<Document> aggs = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    return calcAggDataToMap(Maps.newLinkedHashMap(), aggs);
  }

  /**
   * 获取项目配置 主要是为了区分夏季 非夏季
   *
   * @param subProjectId subProjectId
   */
  public Multimap<String, List<Pair<Integer, Integer>>> getProjectConfig(Long subProjectId) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("subProjectId").is(subProjectId)),
        Aggregation.unwind("$electricPricePeriod"),
        Aggregation.project().andExclude("_id").and("$electricPricePeriod.periodName").as("periodName")
            .and("$electricPricePeriod.seasonPeriod").as("seasonPeriod"),
        Aggregation.project("periodName").and("$seasonPeriod.monthFrom").as("monthFrom")
            .and("$seasonPeriod.monthTo").as("monthTo")
    );
    AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(agg, "projectConfig", Document.class);

    Multimap<String, List<Pair<Integer, Integer>>> map = ArrayListMultimap.create();

    for (Document doc : aggregationResults) {
      String periodName = doc.getString("periodName");
      List<Integer> froms = doc.get("monthFrom", List.class);
      List<Integer> tos = doc.get("monthTo", List.class);
      if (StringUtils.contains(periodName, "非夏季")) {
        map.put("nosummer", toPairs(froms, tos));
      } else {
        map.put("summer", toPairs(froms, tos));
      }
    }
    return map;
  }

  /**
   * 计算各时段电价(夏季/非夏季都一样)
   *
   * @param subProjectId subProjectId
   */
  public Map<String, Double> calcTimePeriodElectricPrice(Long subProjectId) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("subProjectId").is(subProjectId)),
        Aggregation.unwind("$electricPricePeriod"),
        Aggregation.project().andExclude("_id")
            .and("$electricPricePeriod.peakPrice").as("peakPrice")
            .and("$electricPricePeriod.valleyPrice").as("valleyPrice")
            .and("$electricPricePeriod.flatPrice").as("flatPrice")
            .and("$electricPricePeriod.aiguillePrice").as("aiguillePrice")
    );
    AggregationResults<Document> aggResults = mongoTemplate.aggregate(agg, "projectConfig", Document.class);
    Document doc = aggResults.iterator().next();
    return (Map<String, Double>) JSONObject.parseObject(doc.toJson(), Map.class);
  }

  /**
   * 获取各时段(包括夏季 非夏季)
   *
   * @param subProjectId subProjectId
   */
  public List<Map<String, List<Pair<Integer, Integer>>>> getTimePeriod(Long subProjectId) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("subProjectId").is(subProjectId)),
        Aggregation.unwind("$electricPricePeriod"),
        Aggregation.project().andExclude("_id").and("$electricPricePeriod.periodName").as("periodName")
            .and("$electricPricePeriod.timePeriod").as("timePeriod"),
        Aggregation.unwind("$timePeriod"),
        Aggregation.group("$periodName", "$timePeriod.typeName")
            .push("$timePeriod.timeFrom").as("timeFrom")
            .push("$timePeriod.timeTo").as("timeTo")
    );
    List<Map<String, List<Pair<Integer, Integer>>>> maps = new ArrayList<>();
    AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(agg, "projectConfig", Document.class);
    for (Document doc : aggregationResults) {
      List<Integer> timeFrom = doc.get("timeFrom", List.class);
      List<Integer> timeTo = doc.get("timeTo", List.class);
      String periodName = doc.getString("periodName");
      String typeName = doc.getString("typeName");
      Map<String, List<Pair<Integer, Integer>>> map = calcTimeStage(periodName, typeName, timeFrom, timeTo);
      maps.add(map);
    }
    return maps;
  }

  /**
   * 根据isSummer 返回 对应的 时间段数据
   *
   * @param subProjectId subProjectId
   * @return List<Map < String, List < Pair < Integer, Integer>>>>
   */
  public List<Map<String, List<Pair<Integer, Integer>>>> splitTimePeriod(Long subProjectId, boolean isSummer) {
    List<Map<String, List<Pair<Integer, Integer>>>> noSummerTimePair = new ArrayList<>();
    List<Map<String, List<Pair<Integer, Integer>>>> summerTimePair = new ArrayList<>();
    for (Map<String, List<Pair<Integer, Integer>>> timePeriod : getTimePeriod(subProjectId)) {
      for (Map.Entry<String, List<Pair<Integer, Integer>>> entry : timePeriod.entrySet()) {
        String key = entry.getKey();
        if (StringUtils.startsWith(key, "summer")) {
          summerTimePair.add(timePeriod);
        } else {
          noSummerTimePair.add(timePeriod);
        }
      }
    }
    if (isSummer) {
      return summerTimePair;
    } else {
      return noSummerTimePair;
    }
  }

  /**
   * 获取各时段电价
   *
   * @param subProjectId subProjectId
   * @return List<Map < String, Double>>
   */
  public Map<String, BigDecimal> getTimePeriodElectricPrice(Long subProjectId) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("subProjectId").is(subProjectId)),
        Aggregation.unwind("$electricPricePeriod"),
        Aggregation.project().andExclude("_id")
            .and("$electricPricePeriod.peakPrice").as("peakPrice")
            .and("$electricPricePeriod.flatPrice").as("flatPrice")
            .and("$electricPricePeriod.valleyPrice").as("valleyPrice")
            .and("$electricPricePeriod.aiguillePrice").as("aiguillePrice")
    );
    AggregationResults<Document> aggs = mongoTemplate.aggregate(agg, "projectConfig", Document.class);
    if (aggs.getMappedResults().size() > 0) {
      return JSON.parseObject(aggs.iterator().next().toJson(), Map.class);
    }
    return Maps.newHashMap();
  }

  /**
   * 将转换得到的Map数据进行 再次转换
   *
   * @param date         格式必须是 yyyy
   * @param subProjectId subProjectId
   */
  public Map<Integer, List<Map<String, Double>>> flatMapData(String date, Long subProjectId, int flag) {
    Map<Integer, List<Map<String, Double>>> resMap = new LinkedHashMap<>();
    for (int month = 1; month <= 12; month++) {
      boolean isSummerMonth = isSummerMonth(subProjectId, month);
      Map<Integer, Double> mapPair;
      if (month < 10) {
        mapPair = getOriginDataByMonth(date + "/0" + month, subProjectId, flag);
      } else {
        mapPair = getOriginDataByMonth(date + "/" + month, subProjectId, flag);
      }
      //得到这一个月的各个时段
      List<Map<String, List<Pair<Integer, Integer>>>> timePeriodMaps = splitTimePeriod(subProjectId, isSummerMonth);
      List<Map<String, Double>> maps = new ArrayList<>();
      for (Map<String, List<Pair<Integer, Integer>>> map : timePeriodMaps) {
        Map<String, Double> mapObj = new LinkedHashMap<>();
        for (Map.Entry<String, List<Pair<Integer, Integer>>> entry : map.entrySet()) {
          String key = entry.getKey();
          for (Pair<Integer, Integer> pair : entry.getValue()) {
            Integer leftPair = pair.getLeft();
            Integer rightPair = pair.getRight();
            calcJsonData(mapPair.keySet(), leftPair, rightPair, key, mapPair, mapObj);
          }
        }
        maps.add(mapObj);
      }
      if (maps.get(0).size() > 0) {
        resMap.put(month, maps);
      } else {
        resMap.put(month, Lists.newArrayList());
      }
    }
    return resMap;
  }

  /**
   * 转为json数据进行返回
   *
   * @param keys      00-24 小时
   * @param leftPair  夏季/非夏季 各时段的开始时间
   * @param rightPair rightPair 夏季/非夏季 各时段的截止时间
   * @param entryKey  entryKey 夏季/非夏季 各时段 forExample summerFeng代表夏季峰时段 ...
   * @param map       map 夏季/非夏季的 Map 数据
   * @param mapObj    map对象
   */
  private static void calcJsonData(Set<Integer> keys, Integer leftPair, Integer rightPair,
                                   String entryKey, Map<Integer, Double> map, Map<String, Double> mapObj) {
    for (Integer key : keys) {
      if (leftPair <= key && key < rightPair) {
        Double d = map.get(key);
        if (mapObj.get(entryKey) != null) {
          mapObj.put(entryKey, addDouble(d, mapObj.get(entryKey)));
          continue;
        }
        mapObj.put(entryKey, d);
      }
    }
  }

  /**
   * 通过计算将聚合得到的结果数据存入Map中
   *
   * @param map  map
   * @param aggs 聚合结果数据
   * @return Map<Integer, Double> key:小时 value:小时数据总和
   */
  private static Map<Integer, Double> calcAggDataToMap(Map<Integer, Double> map, AggregationResults<Document> aggs) {
    for (Document doc : aggs) {
      Integer h = Integer.parseInt(doc.getString("h"));
      Double diffsum = doc.getDouble("diffsum");
      if (diffsum != null) {
        if (map.containsKey(h)) {
          Double res = addDouble(map.get(h), diffsum);
          map.put(h, res);
        } else {
          map.put(h, diffsum);
        }
      }
    }
    return map;
  }

  /**
   * 防止double计算精度丢失
   *
   * @param x x
   * @param y y
   * @return Double
   */
  private static Double addDouble(Double x, Double y) {
    BigDecimal p1 = new BigDecimal(Double.toString(x));
    BigDecimal p2 = new BigDecimal(Double.toString(y));
    return p1.add(p2).doubleValue();
  }

  /**
   * 计算夏季/非夏季  以及各时间段
   *
   * @param periodName 夏季/非夏季
   * @param typeName   峰时/谷时/尖峰时/平时
   * @param timeFroms  开始时间
   * @param timeTos    结束时间
   */
  private Map<String, List<Pair<Integer, Integer>>> calcTimeStage(String periodName, String typeName, List<Integer> timeFroms, List<Integer> timeTos) {
    Map<String, List<Pair<Integer, Integer>>> map = new LinkedHashMap<>();
    if (StringUtils.contains(periodName, "非夏季")) {
      if (StringUtils.equals("峰时段", typeName)) {
        map.put("nosummerFeng", toPairs(timeFroms, timeTos));
      } else if (StringUtils.equals("平时段", typeName)) {
        map.put("nosummerPing", toPairs(timeFroms, timeTos));
      } else {
        map.put("nosummerGu", toPairs(timeFroms, timeTos));
      }
    } else {
      if (StringUtils.equals("峰时段", typeName)) {
        map.put("summerFeng", toPairs(timeFroms, timeTos));
      } else if (StringUtils.equals("平时段", typeName)) {
        map.put("summerPing", toPairs(timeFroms, timeTos));
      } else if (StringUtils.equals("谷时段", typeName)) {
        map.put("summerGu", toPairs(timeFroms, timeTos));
      } else {
        map.put("summerJF", toPairs(timeFroms, timeTos));
      }
    }
    return map;
  }

  /**
   * 转化list数据
   *
   * @param timeFroms timeFroms
   * @param timeTos   timeTos
   * @return List<Pair < Integer, Integer>>
   */
  private static List<Pair<Integer, Integer>> toPairs(List<Integer> timeFroms, List<Integer> timeTos) {
    List<Pair<Integer, Integer>> pairs = new ArrayList<>();
    for (int i = 0; i < timeFroms.size(); i++) {
      Pair<Integer, Integer> pair = Pair.of(splitInt2(timeFroms.get(i)), splitInt2(timeTos.get(i)));
      pairs.add(pair);
    }
    return pairs;
  }

  /**
   * 给老子切割Integer
   *
   * @param i i
   * @return Integer
   */
  private static Integer splitInt2(Integer i) {
    if (i == 0 || i < 13) {
      return i;
    }
    String istr = String.valueOf(i);
    String substr = istr.substring(istr.length() - 2);
    if (StringUtils.equals("00", substr)) {
      return Integer.parseInt(StringUtils.removeEnd(istr, substr));
    }
    if (StringUtils.equals("59", substr)) {
      return Integer.parseInt(StringUtils.removeEnd(istr, substr)) + 1;
    }
    return Integer.parseInt(istr.substring(0, 2));
  }

  /**
   * 判断传入的yyyy/MM 中的MM 是否属于夏季月份
   *
   * @param month MM
   * @return boolean
   */
  public boolean isSummerMonth(Long subProjectId, int month) {
    Multimap<String, List<Pair<Integer, Integer>>> projectConfig = this.getProjectConfig(subProjectId);
    List<Pair<Integer, Integer>> summerMonths = projectConfig.get("summer").stream().flatMap(List::stream).collect(toList());
    for (Pair<Integer, Integer> sm : summerMonths) {
      if (month >= sm.getLeft() && month <= sm.getRight()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 按天分组 Map<k,v> k:day value:hour
   *
   * @param map 传入的map参数
   */
  private static Multimap<Integer, Map<Integer, Double>> groupByDayWithHour(Map<Integer, Double> map) {
    Multimap<Integer, Map<Integer, Double>> resMap = LinkedHashMultimap.create();
    for (Map.Entry<Integer, Double> entry : map.entrySet()) {
      String kval = String.valueOf(entry.getKey());
      Integer uniqueDay = Integer.parseInt(StringUtils.substring(kval, 0, 8));
      Integer DayHour = Integer.parseInt(StringUtils.substring(kval, 8));
      Double hourVal = entry.getValue();
      Map<Integer, Double> tmpMap = Maps.newLinkedHashMap();
      tmpMap.put(DayHour, hourVal);
      resMap.put(uniqueDay, tmpMap);
    }
    return resMap;
  }

  /**
   * 从数据库中查询所有的记录
   *
   * @return List<ChargeDataByMonth>
   */
  public List<ChargeDataByMonth> findDayList(String date, Long subProejctId) {
    String d = date.replace("/", "-");
    return monthMapper.selectList(new QueryWrapper<ChargeDataByMonth>()
        .eq("sub_project_id", subProejctId)
        .like("date", d));
  }

  /**
   * 从数据库中查询所有的记录
   *
   * @return List<ChargeDataByYear>
   */
  public List<ChargeDataByYear> findMonthList(String date, Long subProejctId) {
    return yearMapper.selectList(new QueryWrapper<ChargeDataByYear>()
        .eq("sub_project_id", subProejctId)
        .like("month", date));
  }

  /**
   * 将数据存储到MySQL
   *
   * @param date         如果是月份数据  格式必须是 yyyy/MM 则存储到 t_chargedata_bymonth ; 否则 表示年份数据 格式必须是 yyyy 存储到 t_chargedata_byyear
   * @param subProjectId subProjectId
   * @param flag         判断是按月分组还是按年分组 1: 按月分组 2:按年分组
   */
  @Transactional
  public void saveByDataToDb(String date, Long subProjectId, int flag) {
    //获取当前天数 跟 月份数
    int currentMonth = LocalDate.now().getMonthValue();
    //获取各时段电价
    double peakPrice = 0d, flatPrice = 0d, valleyPrice = 0d, aiguillePrice = 0d;
    Map<String, BigDecimal> electricPriceMap = getTimePeriodElectricPrice(subProjectId);
    //峰时段电价
    BigDecimal pp = electricPriceMap.get("peakPrice");
    if (pp != null) {
      peakPrice = pp.doubleValue();
    }
    //平时段电价
    BigDecimal fp = electricPriceMap.get("flatPrice");
    if (fp != null) {
      flatPrice = fp.doubleValue();
    }
    //谷时段电价
    BigDecimal vp = electricPriceMap.get("valleyPrice");
    if (vp != null) {
      valleyPrice = vp.doubleValue();
    }
    //尖峰时段电价
    BigDecimal ap = electricPriceMap.get("aiguillePrice");
    if (ap != null) {
      aiguillePrice = ap.doubleValue();
    }
    List<Double> electricPrices = Lists.newArrayList(peakPrice, flatPrice, valleyPrice, aiguillePrice);
    //flag==1 表示按月分组
    if (flag == 1) {
      boolean isSummerMonth = isSummerMonth(subProjectId, splitMonth(date));
      List<ChargeDataByMonth> list = findDayList(date, subProjectId);
      List<String> dateLists = list.stream().map(ChargeDataByMonth::getDate).collect(toList());
      //获取每天每小时各时段的数据
      Multimap<Integer, Map<String, Double>> dataMap = mergeGroupDataByDay(date, subProjectId, 0);
      for (Integer key : dataMap.keySet()) {
        ChargeDataByMonth dayData = new ChargeDataByMonth();
        dayData.setSubProjectId(subProjectId);
        dayData.setDate(integerToStr(key));
        List<Map<String, Double>> mapList = new ArrayList<>(dataMap.get(key));
        for (Map<String, Double> map : mapList) {
          extractM1(map, dayData, isSummerMonth, electricPrices);
        }
        dayData.setDaySum(addListDouble(Lists.newArrayList(dayData.getFengVal(), dayData.getPingVal(), dayData.getGuVal(), dayData.getJfengVal())));
        Double sumPrice = addListDouble(Lists.newArrayList(dayData.getFengPeriodPrice(), dayData.getPingPeriodPrice(), dayData.getGuPeriodPrice(), dayData.getJfengPeriodPrice()));
        dayData.setDayElectricPrice(convertPrecision(sumPrice));
        dayData.setFengPrice(peakPrice);
        dayData.setPingPrice(flatPrice);
        dayData.setGuPrice(valleyPrice);
        String dt = dayData.getDate();
        if (!dateLists.contains(dt) && mapList.size() > 0 && !StringUtils.equalsIgnoreCase(dt, LocalDate.now().toString())) {
          monthMapper.insert(dayData);
        }
      }
    } else if (flag == 2) {//表示按年分组
      List<ChargeDataByYear> monthList = findMonthList(date, subProjectId);
      List<String> dateLists = monthList.stream().map(ChargeDataByYear::getMonth).collect(toList());
      Map<Integer, List<Map<String, Double>>> dataMap = flatMapData(date, subProjectId, 0);
      for (Map.Entry<Integer, List<Map<String, Double>>> entry : dataMap.entrySet()) {
        ChargeDataByYear monthData = new ChargeDataByYear();
        monthData.setMonth(date + "-" + entry.getKey());
        monthData.setSubProjectId(subProjectId);
        List<Map<String, Double>> maps = entry.getValue();
        for (Map<String, Double> map : maps) {
          boolean isSummerMonth = isSummerMonth(subProjectId, entry.getKey());
          extractM1(map, monthData, isSummerMonth, electricPrices);
        }
        monthData.setMonthSum(addListDouble(Lists.newArrayList(monthData.getFengVal(), monthData.getPingVal(), monthData.getGuVal(), monthData.getJfengVal())));
        Double sumPrice = addListDouble(Lists.newArrayList(monthData.getFengPeriodPrice(), monthData.getPingPeriodPrice(), monthData.getGuPeriodPrice(), monthData.getJfengPeriodPrice()));
        monthData.setMonthElectricPrice(convertPrecision(sumPrice));
        monthData.setFengPrice(peakPrice);
        monthData.setPingPrice(flatPrice);
        monthData.setGuPrice(valleyPrice);
        if (!dateLists.contains(monthData.getMonth()) && maps.size() > 0 && currentMonth > entry.getKey()) {
          yearMapper.insert(monthData);
        }
      }
    }
  }

  /**
   * 将方法进行一个简单的抽取 防止一个方法过于臃肿
   *
   * @param map      数据Map
   * @param data     实体Bean
   * @param issummer 是否是夏季月份
   * @param list     电价列表
   */
  private void extractM1(Map<String, Double> map, BaseChargeData data, boolean issummer, List<Double> list) {
    Double fprice = null, pprice = null, gprice = null, jfprice = null;
    if (issummer) {
      Double summerFeng = map.get("summerFeng");
      Double summerPing = map.get("summerPing");
      Double summerGu = map.get("summerGu");
      Double summerJF = map.get("summerJF");
      if (summerFeng != null) {
        data.setFengVal(summerFeng);
        fprice = calcDayTimePeriodElectricPrice(summerFeng, list.get(0));
      }
      if (summerPing != null) {
        data.setPingVal(summerPing);
        pprice = calcDayTimePeriodElectricPrice(summerPing, list.get(1));
      }
      if (summerGu != null) {
        data.setGuVal(summerGu);
        gprice = calcDayTimePeriodElectricPrice(summerGu, list.get(2));
      }
      if (summerJF != null) {
        data.setJfengVal(summerJF);
        jfprice = calcDayTimePeriodElectricPrice(summerJF, list.get(3));
      }
      data.setJfengPrice(list.get(3));
      data.setJfengPeriodPrice(jfprice);
    } else {
      Double nosummerFeng = map.get("nosummerFeng");
      Double nosummerPing = map.get("nosummerPing");
      Double nosummerGu = map.get("nosummerGu");
      if (nosummerFeng != null) {
        data.setFengVal(nosummerFeng);
        fprice = calcDayTimePeriodElectricPrice(nosummerFeng, list.get(0));
      }
      if (nosummerPing != null) {
        data.setPingVal(nosummerPing);
        pprice = calcDayTimePeriodElectricPrice(nosummerPing, list.get(1));
      }
      if (nosummerGu != null) {
        data.setGuVal(nosummerGu);
        gprice = calcDayTimePeriodElectricPrice(nosummerGu, list.get(2));
      }
    }
    if (fprice != null) {
      data.setFengPeriodPrice(fprice);
    }
    if (pprice != null) {
      data.setPingPeriodPrice(pprice);
    }
    if (gprice != null) {
      data.setGuPeriodPrice(gprice);
    }
  }

  /**
   * @param list 数据列表
   * @return Double 和
   */
  private static Double addListDouble(List<Double> list) {
    return list.stream().filter(Objects::nonNull).mapToDouble(o -> o).sum();
  }

  /**
   * 计算时段总耗电量电价  电价*时段耗电量
   *
   * @param x 耗电量
   * @param y 时段电价
   * @return 乘积
   */
  private static Double calcDayTimePeriodElectricPrice(Double x, Double y) {
    if (x == null || y == null) {
      return 0d;
    }
    DecimalFormat df = new DecimalFormat("#.00");
    return Double.valueOf(df.format(x * y));
  }

  /**
   * Internel use for example
   * 20201009 -->  2020-10-09
   *
   * @param i 整形数据
   * @return 转换格式后的字符串
   */
  private static String integerToStr(Integer i) {
    String str = String.valueOf(i);
    return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6);
  }

  /**
   * 主要是从 yyyy/MM 的格式日期中分隔出 MM (月份)
   *
   * @param date 前端传入的日期 必须是 yyyy/MM
   * @return MM (月份)
   */
  private static Integer splitMonth(String date) {
    return Integer.parseInt(date.split("/")[1]);
  }

  /**
   * 转换一下 Double 数据类型的精度
   *
   * @param x x
   * @return res
   */
  private static Double convertPrecision(Double x) {
    DecimalFormat df = new DecimalFormat("#.00");
    return Double.valueOf(df.format(x));
  }
}
