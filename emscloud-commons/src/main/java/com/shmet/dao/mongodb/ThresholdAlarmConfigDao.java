package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.entity.mongo.ThresholdAlarmConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class ThresholdAlarmConfigDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;

    String key = "ThresholdAlarmConfigDao.findByDeviceNoAndTagCode";
  
    public ThresholdAlarmConfig findByDeviceNoAndTagCode(Long deviceNo, String tagCode) {
        String innerkeyItem = deviceNo.toString() + "." + tagCode;
        ThresholdAlarmConfig data = new ThresholdAlarmConfig();
        if (!redisUtil.hHasKey(key, innerkeyItem)) {
            Query query = Query.query(Criteria.where("deviceNo").is(deviceNo).and("tagCode").is(tagCode));
            data = mongoTemplate.findOne(query, ThresholdAlarmConfig.class);
            if (data == null) {
                data = new ThresholdAlarmConfig();
            }
            redisUtil.hset(key, innerkeyItem, data, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        } else {
            data = (ThresholdAlarmConfig) redisUtil.hget(key, innerkeyItem, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return data;
    }

    public void save(ThresholdAlarmConfig config) {
        String innerkeyItem = config.getDeviceNo().toString() + "." + config.getTagCode();
        mongoTemplate.save(config);
        redisUtil.hset(key, innerkeyItem, config, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

}
