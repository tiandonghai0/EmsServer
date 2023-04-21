package com.shmet.redis;

import com.corundumstudio.socketio.SocketIOClient;
import com.shmet.helper.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 专门为适应国开告警新增的缓存(临时添加 后续需要优化)
 *
 * @author
 */
@Component
public class GkSessionCache {
  @Autowired
  RedisUtil redisUtil;

  final String sessionKey = "GkSessionCache.websocketsession";

  final long timeout = 300;

  public void setCache(SocketIOClient client, Object data) {
    redisUtil.hset(sessionKey, client.getSessionId().toString(), data, timeout);
  }

  public Object getAllCache() {
    return redisUtil.hmget(sessionKey, timeout);
  }

  public void delCache(SocketIOClient client) {
    redisUtil.hdel(sessionKey, client.getSessionId().toString());
  }
}
