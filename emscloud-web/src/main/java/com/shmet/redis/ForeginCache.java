package com.shmet.redis;

import com.shmet.helper.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ForeginCache {
  @Autowired
  RedisUtil redisUtil;

  final String foreginKey = "ForeginCache.Token";

  public void setTokenCache(String data, Integer outTime) {
    //redisUtil.hset(foreginKey,"token",data,outTime);
    redisUtil.set(foreginKey, data, outTime);
  }

  public String getTokenCache() {
    Long outTime = redisUtil.getExpire(foreginKey);
    return (String) redisUtil.get(foreginKey, outTime);
  }

}
