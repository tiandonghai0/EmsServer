package com.shmet.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.helper.redis.RedisUtil;

@Component
public class AcconeMsgRedisDao {
  @Autowired
  private RedisUtil redisUtil;

  final String key_msgdata = "AcconeMsgRedisDao.saveAcconeMsgCache";

  final String key_sessiontime = "AcconeMsgRedisDao.saveAcconeSessionTime";

  final long timeout_msgdata = -1;

  final long timeout_sessiontime = 600;

  public void saveAcconeMsgCache(Long acconeId, int commandType, Object value) {
    redisUtil.hset(key_msgdata, getInnerKey(acconeId, commandType), value, timeout_msgdata);
  }

  public Object getAcconeMsg(Long acconeId, int commandType) {
    return redisUtil.hget(key_msgdata, getInnerKey(acconeId, commandType), timeout_msgdata);
  }

  public void delAcconeMsg(Long acconeId, int commandType) {
    redisUtil.hdel(key_msgdata, getInnerKey(acconeId, commandType));
  }

  public void saveAcconeSessionTime(Long acconeId, int commandType, Long timestamp) {
    redisUtil.hset(key_sessiontime, getInnerKey(acconeId, commandType), timestamp, timeout_sessiontime);
  }

  public Long getAcconeSessionTime(Long acconeId, int commandType) {
    Long rst = null;
    Object temp = redisUtil.hget(key_sessiontime, getInnerKey(acconeId, commandType), timeout_sessiontime);
    if (temp != null) {
      rst = (Long) temp;
    }
    return rst;
  }

  private String getInnerKey(Long acconeId, int commandType) {
    return acconeId + "." + commandType;
  }
}
