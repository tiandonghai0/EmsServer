package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.entity.mongo.StrategyConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class StrategyConfigDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;
  
    String key = "StrategyConfigDao.findByAcconeId";

    public StrategyConfig findByAcconeId(Long acconeId) {
        StrategyConfig data = null;
        if (!redisUtil.hHasKey(key, acconeId.toString())) {
            Query query = Query.query(Criteria.where("acconeId").is(acconeId));
            data = mongoTemplate.findOne(query, StrategyConfig.class);
            if (data != null) {
                redisUtil.hset(key, acconeId.toString(), data, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
            }
        } else {
            data = (StrategyConfig) redisUtil.hget(key, acconeId.toString(), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return data;
    }

    public void save(StrategyConfig config) {
        mongoTemplate.save(config);
        redisUtil.hset(key, config.getAcconeId().toString(), config, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

}
