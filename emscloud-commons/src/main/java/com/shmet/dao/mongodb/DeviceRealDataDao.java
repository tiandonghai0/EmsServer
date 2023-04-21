package com.shmet.dao.mongodb;

import java.util.*;

import cn.hutool.core.lang.Pair;
import com.shmet.dao.mongodb.util.MongoSortUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.DateTimeUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class DeviceRealDataDao extends BatchUpdateDao {

  private final String key = "DeviceRealDataDao.findOneByDeviceHour";

  private final String key_firstdayData = "DeviceRealDataDao.findByDeviceDayFirstData";

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private MongoTemplate mongoTemplate;

  public List<DeviceRealData> findAllByDeviceTime(String deviceNo, String startTime, String endTime) {
    Query query = parseParam(deviceNo, startTime, endTime);
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public List<DeviceRealData> findAllDeviceByTime(String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("hour").lte(to).gte(from));
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public DeviceRealData findOneByDeviceHour(long deviceNo, int hour) {
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("hour").is(hour));
    return mongoTemplate.findOne(query, DeviceRealData.class);
  }

  public DeviceRealData findOneByDeviceHour(String deviceNo, String hour) {
    long deviceNoLong = Long.parseLong(deviceNo);
    long hourLong = Long.parseLong(hour);
    String date = DateTimeUtils.date2String(new Date(), DateTimeUtils.FORMAT_YYYYMMDDHH);
    String itemKey = deviceNo + "." + hour;
    Object data = redisUtil.hget(key, itemKey, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    String preHour =
        DateTimeUtils.dateCalc(date, Calendar.HOUR_OF_DAY, -1, DateTimeUtils.FORMAT_YYYYMMDDHH);
    DeviceRealData rst;
    if (data == null || (date.equals(hour) || date.equals(preHour))) {
      Query query =
          Query.query(Criteria.where("deviceNo").is(deviceNoLong).and("hour").is(hourLong));
      rst = mongoTemplate.findOne(query, DeviceRealData.class);
      if (rst != null) {
        redisUtil.hdel(key, itemKey);
        redisUtil.hset(key, itemKey, rst, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
      }
    } else {
      rst = (DeviceRealData) data;
    }
    return rst;
  }

  public List<DeviceRealData> findByDeviceLastHours(Long deviceNo, int count) {
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo));
    query.limit(count);
    query.with(Sort.by(new Order(Direction.DESC, "hour")));
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public List<DeviceRealData> findByDeviceLastHours(Long deviceNo,Long hours, int count) {
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("hour").lte(hours));
    query.limit(count);
    query.with(Sort.by(new Order(Direction.DESC, "hour")));
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public List<DeviceRealData> findByDeviceFirstHours(Long deviceNo,Long hours, int count) {
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("hour").gte(hours));
    query.limit(count);
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public RealDataItem findByDeviceDayFirstData(long deviceNo, String day, String tagCode) {
    String innerKey = deviceNo + Consts.KEY_SEP + tagCode + Consts.KEY_SEP + day;
    if (redisUtil.hHasKey(key_firstdayData, innerKey)) {
      return (RealDataItem) redisUtil.hget(key_firstdayData, innerKey,
          Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }
    RealDataItem realDataItem = null;
    long startTime = Long.parseLong(day + "00");
    long endTime = Long.parseLong(day + "23");
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("hour").lte(endTime).gte(startTime));
    query.limit(1);
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    List<DeviceRealData> firstData = mongoTemplate.find(query, DeviceRealData.class);
    if (firstData.size() > 0) {
      DeviceRealData deviceRealData = firstData.get(0);
      if (deviceRealData != null) {
        List<RealMetricsItem> realMetricsList = deviceRealData.getMetrics();
        realMetricsList.sort(Comparator.comparing(RealMetricsItem::getTimestamp));
        RealMetricsItem realMetricsItem = realMetricsList.get(0);
        if (deviceRealData.getStatistics() != null && !deviceRealData.getStatistics().isEmpty()
            && deviceRealData.getStatistics().get(tagCode) != null) {
          Object data =
              deviceRealData.getStatistics().get(tagCode).get(Consts.DIFFSUM_STATISTICS_FIRST);
          if (data != null) {
            realDataItem = new RealDataItem();
            realDataItem.setTagCode(tagCode);
            realDataItem.setDeviceNo(String.valueOf(deviceNo));
            realDataItem.setDateTime(String.valueOf(realMetricsItem.getTimestamp()));
            realDataItem.setData(data);
            redisUtil.hset(key_firstdayData, innerKey, realDataItem,
                Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
          }
        }
      }
    }
    return realDataItem;
  }

  public List<DeviceRealData> findByDateTimeWithOutMetrics(List<Long> deviceNoList, String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("hour").lte(to).gte(from).and("statistics").exists(true).and("deviceNo").in(deviceNoList));
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    query.fields().exclude("metrics");
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public List<DeviceRealData> findByDateTimeWithOutMetrics(String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("hour").lte(to).gte(from).and("statistics").exists(true));
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    query.fields().exclude("metrics");
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public List<DeviceRealData> findAllByDeviceTimeWithOutMetrics(String deviceNo, String startTime,
                                                                String endTime) {
    Query query = parseParam(deviceNo, startTime, endTime);
    query.fields().exclude("metrics");
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  private Query parseParam(String deviceNo, String startTime, String endTime) {
    long deviceNoLong = Long.parseLong(deviceNo);
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNoLong).and("hour").lte(to).gte(from));
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    return query;
  }

  public boolean checkExists(long deviceNo, int hour, long timestamp) {
    Query query =
        Query.query(Criteria.where("deviceNo").is(deviceNo).and("hour").is(hour)
            .and("metrics.timestamp").is(timestamp));
    return mongoTemplate.exists(query, DeviceRealData.class);
  }

  /**
   * @param deviceNoList eg. [202010010010006, 202010010010009]
   * @param tagCodeList  eg. ["EPE","EPI"]
   * @param startTime    eg. Integer.min
   * @param endTime      eg. Integer.max
   * @param skip         跳过多少
   * @param limit        最大限制多少
   * @param sortFlag     排序相关
   * @param sortMethods  按照time（高位）， deviceNo（低位），0代表正排序，1代表倒排序，共有00,01,10,11四种情况
   * @return 检索结果
   */
  public AggregationResults<Document> aggregateHourByDeviceTimeTagForPage(Collection<Long> deviceNoList, Collection<String> tagCodeList, long startTime,
                                                                          long endTime, long skip, long limit, int sortFlag, int sortMethods) {
    Sort sort;
    Sort.Order orderTime = MongoSortUtil.sortOrderTime(sortMethods, null);
    Sort.Order orderDeviceNo = MongoSortUtil.sortOrderDeviceNo(sortMethods, null);

    if (sortFlag == 2) {
      sort = Sort.by(orderTime, orderDeviceNo);
    } else {
      sort = Sort.by(orderDeviceNo, orderTime);
    }

    Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("hour").lte(endTime).gte(startTime)),
            Aggregation.project("deviceNo").and("hour").as("time")
                .and((ObjectOperators.valueOf("statistics").toArray())).as("statistics")
                .andExclude("_id"),
            Aggregation.unwind("statistics"),
            Aggregation.match(Criteria.where("statistics.k").in(tagCodeList)),
            Aggregation.sort(sort),
            Aggregation.facet(Aggregation.skip(skip), Aggregation.limit(limit)).as("result")
                .and(Aggregation.count().as("count")).as("totalCount")
        );
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  public AggregationResults<Document> aggregateRealDataByDeviceTimeTagForPage(
      Collection<Long> deviceNoList, Collection<String> tagCodeList, long startTime, long endTime,
      long skip, long limit, int sortFlag, int sortMethods) {
    Sort sort = MongoSortUtil.sortOrder(sortFlag, sortMethods);
    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("hour").lte(endTime)
                .gte(startTime)),
            Aggregation.project().andExclude("statistics").andExclude("_id"),
            Aggregation.unwind("metrics"),
            Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp")
                .and("metrics.datas").as("datas"),
            Aggregation.project("deviceNo").and("timestamp").as("timestamp")
                .and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
            Aggregation.unwind("datas"),
            Aggregation.match(Criteria.where("datas.k").in(tagCodeList)),
            Aggregation.sort(sort),
            Aggregation.facet(Aggregation.skip(skip), Aggregation.limit(limit)).as("result")
                .and(Aggregation.count().as("count")).as("totalCount")
        );
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  public AggregationResults<Document> aggregateRealDataByDeviceNoLatest(
      Collection<Long> deviceNoList, long startTime, long endTime) {
    Sort.Order orderTime = new Sort.Order(Sort.Direction.DESC, "hour");
    Sort.Order orderDeviceNo = new Sort.Order(Sort.Direction.ASC, "deviceNo");
    Sort sort = Sort.by(orderTime, orderDeviceNo);

    Aggregation agg =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("hour").lte(endTime)
                .gte(startTime)),
            Aggregation.project().andExclude("metrics").andExclude("_id"),
            Aggregation.sort(sort),
            Aggregation.group("deviceNo").first("deviceNo").as("deviceNo")
                .first("hour").as("hour").first("subProjectId").as("subProjectId")
                .first("acconeId").as("acconeId").first("statistics").as("statistics")
        );
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  public AggregationResults<Document> aggregateRealDataByDate(
          Collection<Long> deviceNoList,long startTime, long endTime) {
    Collection<String> tagcode=new ArrayList<>();
    tagcode.add("S_STATUS");
    Sort sort = MongoSortUtil.sortOrder(1, 1);
    Aggregation agg =
            Aggregation.newAggregation(
                    Aggregation.match(Criteria.where("deviceNo").in(deviceNoList).and("hour").lte(endTime)
                            .gte(startTime)),
                    //Aggregation.project().andExclude("statistics").andExclude("_id"),
                    Aggregation.unwind("metrics"),
                    Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp")
                            .and("metrics.datas").as("datas"),
                    Aggregation.project("deviceNo").and("timestamp").as("timestamp")
                            .and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
                    Aggregation.unwind("datas"),
                   //Aggregation.match(Criteria.where("datas.s").in(tagcode)),
                    //Aggregation.sort(sort)
                    Aggregation.facet(Aggregation.skip(0), Aggregation.limit(1000000)).as("result")
                           .and(Aggregation.count().as("count")).as("totalCount")
            );
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  public List<DeviceRealData> findByDateTimeWithOutMetrics2(List<Long> deviceNoList, String startTime, String endTime) {
    long from = Long.parseLong(startTime);
    long to = Long.parseLong(endTime);
    Query query = Query.query(Criteria.where("hour").lte(to).gte(from).and("deviceNo").in(deviceNoList));
    query.with(Sort.by(new Order(Direction.ASC, "hour")));
    query.fields();
    return mongoTemplate.find(query, DeviceRealData.class);
  }

  public void save(DeviceRealData deviceRealData) {
    mongoTemplate.save(deviceRealData);
  }

  public List<DeviceRealData> getTagcodesByNo(List<Long>  deviceNos, Pair<Long, Long> pairTime){
    List<DeviceRealData> datas = mongoTemplate.find(
            Query.query(
                    Criteria.where("deviceNo").in(deviceNos)
                            .and("hour")
                            .gte(pairTime.getKey()).lt(pairTime.getValue())
            ).with(Sort.by(new Order(Direction.ASC, "hour"))),
            DeviceRealData.class,
            "deviceRealData"
    );
    return  datas;
  }

}
