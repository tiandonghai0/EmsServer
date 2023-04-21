package com.shmet.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.helper.redis.RedisUtil;

@Component
public class MailRedisDao {
    @Autowired
    private RedisUtil redisUtil;

    final String key_mail = "MailRedisDao.saveMailCache";

    final long timeout = 3600;

    public void saveMailCache(Long deviceNo, String tagCode, Object value) {
        redisUtil.hset(key_mail, getInnerKey(deviceNo, tagCode), value, timeout);
    }

    public Object getMailCache(Long deviceNo, String tagCode) {
        return redisUtil.hget(key_mail, getInnerKey(deviceNo, tagCode), timeout);
    }

    private String getInnerKey(Object... args) {
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (Object val : args) {
            if (isFirst) {
                sb.append(val);
                isFirst = false;
            } else {
                sb.append(".").append(val);
            }
        }
        return sb.toString();
    }
}
