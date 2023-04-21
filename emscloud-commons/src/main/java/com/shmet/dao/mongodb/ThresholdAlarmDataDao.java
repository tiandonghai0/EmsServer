package com.shmet.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.shmet.DateTimeUtils;
import com.shmet.entity.mongo.ThresholdAlarmData;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class ThresholdAlarmDataDao extends BatchUpdateDao {

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private MongoTemplate mongoTemplate;

  public ThresholdAlarmData findLastAlarmByDeviceNoAndTagCode(Long deviceNo, String tagCode) {
    ThresholdAlarmData alarmData = null;
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("tagCode").is(tagCode));
    query.with(Sort.by(new Order(Direction.DESC, "lastTime")));
    query.limit(1);
    List<ThresholdAlarmData> alarmDataList = mongoTemplate.find(query, ThresholdAlarmData.class);
    if (alarmDataList.size() > 0) {
      alarmData = alarmDataList.get(0);
    }
    return alarmData;
  }

  public void updateAlarmData(ThresholdAlarmData alarmData, RealMetricsItem item) {
    Long firstTime = alarmData.getFirstTime();
    Long lastTime = item.getTimestamp();
    long duration = DateTimeUtils.differFromSecond(firstTime.toString(), lastTime.toString(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
    Query query = Query.query(Criteria.where("deviceNo").is(alarmData.getDeviceNo()).and("tagCode").is(alarmData.getTagCode()).and("firstTime").is(firstTime).and("lastTime").is(lastTime));
    Update update = Update.update("lastTime", item.getTimestamp()).set("duration", duration);
    update.addToSet("metrics", item);
    mongoTemplate.upsert(query, update, ThresholdAlarmData.getCollectionName());
  }

  public void save(ThresholdAlarmData alarmData) {
    mongoTemplate.save(alarmData);
  }
}
