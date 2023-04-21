package com.shmet.handler.v2;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.shmet.ArithUtil;
import com.shmet.utils.DateUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import com.shmet.vo.PhotovoltaicVo;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 光电相关处理业务类
 */
@Service
public class PhotovoltaicService {

  public static final Logger log = LoggerFactory.getLogger(PhotovoltaicService.class);

  @Autowired
  private CommonService commonService;

  @Resource
  private DeviceMapper deviceMapper;

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";

  @Resource
  private MongoTemplate mongoTemplate;

  /**
   * 获取光伏监控数据
   *
   * @param subprojectIds subprojectIds
   * @param type          2 代表广电 3 代表风电
   * @return List<PhotovoltaicVo>
   */
  public List<PhotovoltaicVo> realPhotovoltaicData(List<Long> subprojectIds, int type) {
    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    List<Long> dnos = commonService.photoVoltaicNos(subprojectIds, 1, type);
    List<PhotovoltaicVo> vos = Lists.newArrayList();
    if (CollectionUtils.isNotEmpty(dnos)) {
      dnos.forEach(o -> {
        PhotovoltaicVo vo = new PhotovoltaicVo();
        Device device = deviceMapper.selectOne(new QueryWrapper<Device>().eq("device_no", o));
        if (device != null) {
          vo.setPtaicType(device.getDeviceName());
        }
        //发电功率
        Object pval = stringRedisTemplate.opsForHash().get(REALDATA_CACHE, o + ".P");
        if (pval != null) {
          RealDataItem item = JSON.parseObject((String) pval, RealDataItem.class);
          double d = Double.parseDouble((String) item.getData());
          vo.setP(Math.abs(d));
          if (d > 0 || d < 0) {
            if (type == 4) {
              vo.setRunStatus("充电");
            } else {
              vo.setRunStatus("发电");
            }
          } else {
            vo.setRunStatus("停机");
          }
        }
        //电压
        Object uval = stringRedisTemplate.opsForHash().get(REALDATA_CACHE, o + ".UA");
        if (uval != null) {
          RealDataItem item = JSON.parseObject((String) uval, RealDataItem.class);
          vo.setU(Double.parseDouble((String) item.getData()));
          String time = DateUtils.strConvertDateStr(item.getDateTime(), "-", ":");
          vo.setRunTime(time);
        }
        //电流
        Object ival = stringRedisTemplate.opsForHash().get(REALDATA_CACHE, o + ".IA");
        if (ival != null) {
          RealDataItem item = JSON.parseObject((String) ival, RealDataItem.class);
          vo.setI(Double.parseDouble((String) item.getData()));
        }
        Map<String, Double> map = queryFromMongo(o);
        vo.setPowerelec(map.get("dayEpi"));

        vos.add(vo);
      });
    }
    return vos;
  }

  public Map<String, Double> queryFromMongo(Long deviceNo) {
    Pair<Long, Long> pair = DateUtils.get00And23();
    Map<String, Double> map = new LinkedHashMap<>();
    AggregationResults<Document> results = getDocuments(deviceNo, pair);

    if (results.getMappedResults().size() > 0) {
      double epi = NumberUtil.convert2(Double.parseDouble(String.valueOf(results.getMappedResults().get(0).get("diffsumEPI"))));
      double epe = NumberUtil.convert2(Double.parseDouble(String.valueOf(results.getMappedResults().get(0).get("diffsumEPE"))));
      map.put("dayEpi", NumberUtil.convert2(Math.abs(ArithUtil.sub(epi, epe))));
    }
    return map;
  }

  private AggregationResults<Document> getDocuments(Long deviceNo, Pair<Long, Long> pair) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").is(deviceNo).and("hour").lte(pair.getRight()).gte(pair.getLeft())),
        Aggregation.group("deviceNo")
            .sum("statistics.EPE.diffsum").as("diffsumEPE")
            .sum("statistics.EPI.diffsum").as("diffsumEPI"));
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  /**
   * 计算实时光电功率已经合并过的(求和)
   */
  public Map<String, Double> calcRealPhotovolP(List<Long> subprojectIds, String date, int deviceModelType) {
    List<Document> docs = getOriginDatas(subprojectIds, date, deviceModelType);
    Map<String, Double> map = new LinkedHashMap<>();
    for (Document doc : docs) {
      String timestamp = doc.getString("timestamp");
      String subStr = StringUtils.substring(timestamp, 8);
      if (StringUtils.endsWithIgnoreCase(timestamp, "00") || StringUtils.endsWithIgnoreCase(timestamp, "30")) {
        if (map.get(timestamp) != null) {
          map.put(timestamp, NumberUtil.convert2(ArithUtil.add(map.get(timestamp), Math.abs(doc.getDouble("v")))));
        } else {
          map.put(timestamp, NumberUtil.convert2(Math.abs(doc.getDouble("v"))));
        }
      }
    }
    return new TreeMap<>(map);
  }

  /**
   * 计算实时光电功率没有合并过的(求和)
   */
  public Map<Long, Map<String, Double>> calcRealPhotovolP2(List<Long> subprojectIds, String date, int deviceModelType) {
    Map<Long, Map<String, Double>> res = Maps.newTreeMap();
    List<Document> docs = getOriginDatas(subprojectIds, date, deviceModelType);
    Map<Long, List<Document>> map = docs.stream().collect(Collectors.groupingBy(o -> groupByDeviceNo(o.getLong("deviceNo"))));
    List<String> times00 = timeLists00();
    List<String> times30 = timeLists30();
    for (Map.Entry<Long, List<Document>> entry : map.entrySet()) {
      Long key = entry.getKey();
      Map<String, Double> doubleMap = new TreeMap<>();
      for (Document doc : entry.getValue()) {
        String timestamp = doc.getString("timestamp");
        String subStr = StringUtils.substring(timestamp, 8);
        calcTimeToMap(doubleMap, times00, doc, timestamp, subStr);
        calcTimeToMap(doubleMap, times30, doc, timestamp, subStr);
      }
      res.put(key, doubleMap);
    }
    return res;
  }

  private static Long groupByDeviceNo(Long deviceNo) {
    return deviceNo;
  }

  public List<Document> getOriginDatas(List<Long> subprojectIds, String date, int deviceModelType) {
    Pair<Long, Long> timepair = DateUtils.convertDateToPair(date);
    List<Long> deviceNos = getDeviceNos(subprojectIds, "1", deviceModelType);
    log.info("getOriginDatas deviceNos : {}", deviceNos);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("deviceNo")
                .in(deviceNos)
                .and("hour").gte(timepair.getLeft()).lte(timepair.getRight())
        ),
        Aggregation.project().andExclude("statistics").andExclude("_id"),
        Aggregation.unwind("metrics"),
        Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp")
            .and("metrics.datas").as("datas"),
        Aggregation.project("deviceNo").and("timestamp").as("timestamp")
            .and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
        Aggregation.unwind("datas"),
        Aggregation.match(Criteria.where("datas.k").is("P")),
        Aggregation.project().and("$deviceNo").as("deviceNo")
            .and(StringOperators.SubstrCP.valueOf("$timestamp").substringCP(0, 12))
            .as("timestamp")
            .and("$datas.v").as("v"),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo", "timestamp"),
        Aggregation.facet(Aggregation.limit(100000)).as("result")
            .and(Aggregation.count().as("count")).as("totalCount")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    return (List<Document>) aggRes.iterator().next().get("result");
  }

  public Map<Integer, Double> caclRealEpi(List<Long> subprojectIds, String date, int deviceModelType) {
    List<Long> deviceNos = getDeviceNos(subprojectIds, "1", deviceModelType);
    //log.info("caclRealEpi deviceNos : {}", deviceNos);
    Pair<Long, Long> pair = DateUtils.convertDateToPair(date);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("deviceNo").in(deviceNos)
                .and("hour").lte(pair.getRight()).gte(pair.getLeft())
        ),
        Aggregation.project("deviceNo", "hour").andExclude("_id")
            .and("$statistics.EPI.diffsum").as("diffsumEPI")
            .and("$statistics.EPE.diffsum").as("diffsumEPE"),
        Aggregation.sort(Sort.Direction.ASC, "deviceNo", "hour")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    List<Document> docs = aggRes.getMappedResults();
    Map<Integer, Double> map = new LinkedHashMap<>();
    for (Document doc : docs) {
      Integer hour = doc.getInteger("hour");
      Double epiVal = doc.getDouble("diffsumEPI");
      Double epeVal = doc.getDouble("diffsumEPE");

      if (epeVal != null && epiVal != null) {
        double absVal = Math.abs(ArithUtil.sub(epeVal, epiVal));
        if (map.get(hour) != null) {
          map.put(hour, ArithUtil.add(map.get(hour), absVal));
        } else {
          map.put(hour, absVal);
        }
      }
    }
    return map;
  }

  private void calcTimeToMap(Map<String, Double> map, List<String> times, Document doc, String timestamp, String subStr) {
    for (String s : times) {
      if (StringUtils.equalsIgnoreCase(s, subStr)) {
        if (map.get(s) != null) {
          map.put(timestamp, ArithUtil.add(map.get(s), Math.abs(doc.getDouble("v"))));
        } else {
          map.put(timestamp, Math.abs(doc.getDouble("v")));
        }
        break;
      } else if (!map.containsKey(StringUtils.substring(timestamp, 0, 8) + s)) {
        map.put(StringUtils.substring(timestamp, 0, 8) + s, 0d);
      }
    }
  }

  public List<Long> getDeviceNos(List<Long> subProjectIds, String deviceModel, int deviceModelType) {
      return deviceMapper.selectList(new QueryWrapper<Device>()
          .eq("device_model_type", deviceModelType)
          .eq("device_model", deviceModel)
          .in("sub_project_id", subProjectIds))
          .stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<String> timeLists00() {
    return Lists.newArrayList(
        "0000", "0100", "0200", "0300",
        "0400", "0500", "0600", "0700",
        "0800", "0900", "1000", "1100",
        "1200", "1300", "1400", "1500",
        "1600", "1700", "1800", "1900",
        "2000", "2100", "2200", "2300"
    );
  }

  public List<String> timeLists30() {
    return Lists.newArrayList(
        "0030", "0130", "0230", "0330",
        "0430", "0530", "0630", "0730",
        "0830", "0930", "1030", "1130",
        "1230", "1330", "1430", "1530",
        "1630", "1730", "1830", "1930",
        "2030", "2130", "2230", "2330"
    );
  }
}
