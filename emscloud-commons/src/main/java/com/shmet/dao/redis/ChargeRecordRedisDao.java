package com.shmet.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.entity.mongo.ChargeEvent;
import com.shmet.helper.redis.RedisUtil;

@Component
public class ChargeRecordRedisDao {
    @Autowired
    private RedisUtil redisUtil;

    final String key = "ChargeRecordRedisDao.saveChargeRecord";
    final String key2 = "ChargeRecordRedisDao.saveChargeRecordId";

    // cache中的数据一直保存
    final long timeout = 1800;

    public void saveChargeEventCache(Integer sendNo, ChargeEvent value) {
        redisUtil.hset(key, String.valueOf(sendNo), value, timeout);
    }

    public ChargeEvent getChargeEventCache(Integer sendNo) {
        ChargeEvent rst = null;
        Object obj = redisUtil.hget(key, String.valueOf(sendNo), timeout);
        if (obj != null) {
            rst = (ChargeEvent) obj;
        }
        return rst;
    }

}
