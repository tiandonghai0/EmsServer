package com.shmet.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.entity.mongo.DeviceConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class DeviceConfigDao {
  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private RedisUtil redisUtil;

  String key = "DeviceConfigDao.findDeviceConfig";

  public DeviceConfig findDeviceConfig(Long deviceNo, String tagCode) {
    String innerkey = deviceNo.toString() + "." + tagCode;
    DeviceConfig data;
    if (!redisUtil.hHasKey(key, innerkey)) {
      Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("tagCode").is(tagCode));
      data = mongoTemplate.findOne(query, DeviceConfig.class);
      if (data != null) {
        redisUtil.hset(key, innerkey, data, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
      }
    } else {
      data = (DeviceConfig) redisUtil.hget(key, innerkey, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }
    return data;
  }

  /**
   * 根据设备编号查询 DeviceConfig 配置列表
   *
   * @param deviceNo 设备编号
   * @return List<DeviceConfig>
   */
  public List<DeviceConfig> findDeviceConfigByDeviceNo(Long deviceNo) {
    Query query = Query.query(Criteria.where("deviceNo").is(deviceNo));
    return mongoTemplate.find(query, DeviceConfig.class);
  }

  public void save(DeviceConfig config) {
    mongoTemplate.save(config);
  }

}
