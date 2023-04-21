package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.AcconeAlarmData;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class AcconeAlarmDataDao extends BatchUpdateDao {

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private MongoTemplate mongoTemplate;
  
  public void save(AcconeAlarmData alarmData) {
    mongoTemplate.save(alarmData);
  }
}
