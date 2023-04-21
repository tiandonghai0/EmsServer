package com.shmet.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOClient;
import com.shmet.helper.redis.RedisUtil;

@Component
public class RealDataByAcconeWebSocketSessionCache {
    @Autowired
    RedisUtil redisUtil;

    final String sessionKey = "RealDataByAcconeWebSocketSessionCache.websocketsession";

    final long timeout = 300;

    public void setCache(SocketIOClient client, Object data) {
        redisUtil.hset(sessionKey, client.getSessionId().toString(), data, timeout);
    }

    public void delCache(SocketIOClient client) {
        redisUtil.hdel(sessionKey, client.getSessionId().toString());
    }

    public Object getAllCache() {
        return redisUtil.hmget(sessionKey, timeout);
    }
}
