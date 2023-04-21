package com.shmet.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOClient;
import com.shmet.helper.redis.RedisUtil;

@Component
public class UpdateAcconeWebSocketSessionCache {
    @Autowired
    RedisUtil redisUtil;

    final String acconeSessionKey = "AcconeWebSocketSessionCache.AcconeSession";

    final String attrKey = "updateAcconeId";

    final long timeout = 300;

    public void setCache(String acconeId, SocketIOClient client) {
        client.set(attrKey, acconeId);
        redisUtil.hset(acconeSessionKey, acconeId, client.getSessionId().toString(), timeout);
    }

    public void delCache(String acconeId) {
        redisUtil.hdel(acconeSessionKey, acconeId);
    }

    public void delCache(SocketIOClient client) {
        String acconeId = client.get(attrKey);
        if (acconeId == null) {
            return;
        }
        redisUtil.hdel(acconeSessionKey, acconeId);
    }

    public String getCache(String acconeId) {
        return (String) redisUtil.hget(acconeSessionKey, acconeId, timeout);
    }
}
