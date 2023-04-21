package com.shmet.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.bean.Ehang365StartStopBean;
import com.shmet.helper.redis.RedisUtil;

@Component
public class ShorePowerRedisDao {
  @Autowired
  private RedisUtil redisUtil;

  final String key = "ShorePowerRedisDao.startinfocache";
  final String key2 = "ShorePowerRedisDao.stopinfocache";
  final long timeout = 20;

  public void saveStartInfoCache(Long acconeId, Integer deviceId, Ehang365StartStopBean value) {
    redisUtil.hset(key, getInnerKey(acconeId, deviceId), value, timeout);
  }

  public Ehang365StartStopBean getStartInfoCache(Long acconeId, Integer deviceId) {
    Object obj = redisUtil.hget(key, getInnerKey(acconeId, deviceId), timeout);
    Ehang365StartStopBean rst = null;
    if (obj != null) {
      rst = (Ehang365StartStopBean) obj;
    }
    return rst;
  }
  
  public void saveStopInfoCache(Long acconeId, Integer deviceId, Ehang365StartStopBean value) {
    redisUtil.hset(key2, getInnerKey(acconeId, deviceId), value, timeout);
  }

  public Ehang365StartStopBean getStopInfoCache(Long acconeId, Integer deviceId) {
    Object obj = redisUtil.hget(key2, getInnerKey(acconeId, deviceId), timeout);
    Ehang365StartStopBean rst = null;
    if (obj != null) {
      rst = (Ehang365StartStopBean) obj;
    }
    return rst;
  }
  
  private String getInnerKey(Long acconeId, Integer deviceId) {
    return new StringBuffer().append(acconeId).append(".").append(deviceId).toString();
  }

}
