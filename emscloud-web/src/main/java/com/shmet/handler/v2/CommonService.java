package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 一些service通用的处理方法
 */
@Service
public class CommonService {

  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * 根据 deviceNo.tagCode 进行精确查找
   */
  public String cursorItem(String deviceNo, String tagCode) {
    Cursor<Map.Entry<Object, Object>> cursor = stringRedisTemplate.opsForHash()
        .scan(
            REALDATA_CACHE,
            ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(deviceNo + "." + tagCode).build()
        );
    if (cursor.hasNext()) {
      return (String) cursor.next().getValue();
    }
    return null;
  }

  /**
   * 根据 deviceNo.tagCode 进行精确查找
   */
  public List<String> cursorItems(List<String> deviceNos, String tagCode) {
    return listCursor(deviceNos, Lists.newArrayList(tagCode));
  }

  public List<String> cursorItems(String deviceNo, List<String> tagCodes) {
    return listCursor(Lists.newArrayList(deviceNo), tagCodes);
  }

  private List<String> listCursor(List<String> deviceNos, List<String> tcodes) {
    List<String> items = new ArrayList<>();
    for (String dno : deviceNos) {
      for (String tcode : tcodes) {
        Cursor<Map.Entry<Object, Object>> cursor = getCursor(dno, tcode);
        if (cursor.hasNext()) {
          items.add((String) cursor.next().getValue());
        }
      }
    }
    return items;
  }


  private Cursor<Map.Entry<Object, Object>> getCursor(String deviceNo, String tcode) {
    return stringRedisTemplate.opsForHash()
        .scan(
            REALDATA_CACHE,
            ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(deviceNo + "." + tcode).build()
        );
  }

  /**
   * 根据 deviceNo.tagCode 进行精确查找
   */
  public List<String> cursorItems(List<Long> deviceNos) {
    List<String> items = new ArrayList<>();
    List<String> keys = deviceNos.stream().map(o -> o + ".*").collect(toList());
    for (String key : keys) {
      Cursor<Map.Entry<Object, Object>> cursor = stringRedisTemplate.opsForHash()
          .scan(
              REALDATA_CACHE,
              ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(key).build()
          );

      while (cursor.hasNext()) {
        items.add((String) cursor.next().getValue());
      }
    }

    return items;
  }

  /**
   * 根据key 进行模糊匹配
   *
   * @param key     deviceNo
   * @param pattern 匹配的格式
   * @return Set<String></>
   * eg 1000+".*"
   */
  public Set<String> listKeysByPattern(String key, String pattern) {
    Cursor<Map.Entry<Object, Object>> cursor = stringRedisTemplate.opsForHash().scan(key,
        ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(pattern).build());
    Set<String> items = Sets.newLinkedHashSet();
    while (cursor.hasNext()) {
      items.add((String) cursor.next().getValue());
    }
    return items;
  }

  /**
   * 根据subprojectId deviceModel type 查询 对应的deviceNos 列表
   *
   * @param subprojectIds subprojectIds
   * @param deviceModel   1 表示电表
   * @param type          0:市电 1:储能 2:光电 3:风电 4:充电桩
   * @return DeviceNos
   */
  public List<Long> photoVoltaicNos(List<Long> subprojectIds, int deviceModel, Integer type) {
    if (type == null) {
      return deviceMapper.selectList(new QueryWrapper<Device>()
          .eq("device_model", deviceModel)
          .in("sub_project_id", subprojectIds)
      ).stream().map(Device::getDeviceNo).collect(Collectors.toList());
    } else {
      return deviceMapper.selectList(new QueryWrapper<Device>()
          .eq("device_model_type", type)
          .eq("device_model", deviceModel)
          .in("sub_project_id", subprojectIds)
      ).stream().map(Device::getDeviceNo).collect(Collectors.toList());
    }
  }

  //按日 dateType=1 2021-04-09 ==> 2021040900
  //按月 dateType=2 2021-04 ==> 2021040100
  //按年 dateType=3 2021 ==> 2021040900
  public MatchOperation basicAgg(String sdate, int dateType, List<Long> deviceNos) {
    if (dateType == 1) {
      long start = Long.parseLong(sdate + "00");
      long end = Long.parseLong(sdate + "24");
      return Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(start).lte(end));
    }
    if (dateType == 2) {
      long month = Long.parseLong(sdate);
      long start = Long.parseLong(sdate + "0100");
      long end = Long.parseLong(sdate + "3124");
      return Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(start).lte(end));
    }
    if (dateType == 3) {
      long start = Long.parseLong(StringUtils.substring(sdate, 0, 4) + "010100");
      long end = Long.parseLong(StringUtils.substring(sdate, 0, 4) + "123124");
      return Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(start).lte(end));
    }
    return Aggregation.match(Criteria.where(""));
  }

  /**
   * 转换时间日期
   */
  public String transferDate(String date, int dateType) {
    //按天
    if (dateType == 1) {
      if (StringUtils.isBlank(date)) {
        //eg. 2020-12-28 --> 20201228
        return StringUtils.replace(LocalDate.now().toString(), "-", "");
      } else {
        return StringUtils.replace(date, "-", "");
      }
    }
    //按月
    if (dateType == 2) {
      if (StringUtils.isBlank(date)) {
        int year = LocalDate.now().getYear();
        int monthValue = LocalDate.now().getMonthValue();
        if (monthValue >= 10) {
          return year + "" + monthValue;
        }
        // 202012
        return year + "0" + monthValue;
      } else {
        return StringUtils.replace(date, "-", "");
      }
    }
    //按年
    if (dateType == 3) {
      if (StringUtils.isBlank(date)) {
        return String.valueOf(Year.now().getValue());
      } else {
        return date;
      }
    }
    return "-1";
  }

  public List<Long> getDeviceNos(List<Long> subprojectIds, int deviceModelType, int deviceModel) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model_type", deviceModelType)
        .eq("device_model", deviceModel)
        .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<Long> getDeviceNos(List<Long> subprojectIds) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<Long> getDeviceNos(List<Long> subprojectIds, int deviceModel) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", deviceModel)
        .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 计算当天收益
   * data 格式必须是 yyyy-MM-dd
   */
  public Map<String, Object> getCurrentDayIncome(String date, List<Long> subprojectIds) {
    Map<String, Object> res = new HashMap<>();

    List<Long> deviceNos = getDeviceNos(subprojectIds);
    Pair<Long, Long> hourPair = getHourOrDefaultHour(date);
    MatchOperation currentMatch = Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(hourPair.getLeft()).lte(hourPair.getRight()));

    ProjectionOperation projectionOperation = Aggregation.project().andExclude("_id")
        .and("$hour").substring(0, 8).as("day")
        .and("$statistics.EPI.price").as("epiPrice")
        .and("$statistics.EPE.price").as("epePrice");
    GroupOperation groupOperation = Aggregation.group("day")
        .sum("epiPrice").as("epipricesum")
        .sum("epePrice").as("epepricesum");
    Aggregation aggCurrent = Aggregation.newAggregation(
        currentMatch,
        projectionOperation,
        groupOperation
    );
    AggregationResults<Document> aggCurrentRes = mongoTemplate.aggregate(aggCurrent, "deviceRealData", Document.class);
    for (Document doc : aggCurrentRes.getMappedResults()) {
      res.put("day", doc.getString("_id"));
      res.put("income", Double.parseDouble(String.valueOf(doc.get("epepricesum"))) - Double.parseDouble(String.valueOf(doc.get("epipricesum"))));
    }
    return res;
  }

  //formattrn yyyyMMddHH
  public Pair<Long, Long> getHourOrDefaultHour(String date) {
    long startHour, endHour;
    //按小时 不传默认是当天
    if (StringUtils.isNotBlank(date)) {
      startHour = Long.parseLong(StringUtils.replace(date, "-", "") + "00");
      endHour = Long.parseLong(StringUtils.replace(date, "-", "") + "24");
    } else {
      startHour = Long.parseLong(StringUtils.replace(LocalDate.now().toString(), "-", "") + "00");
      endHour = Long.parseLong(StringUtils.replace(LocalDate.now().toString(), "-", "") + "24");
    }

    return Pair.of(startHour, endHour);
  }
}
