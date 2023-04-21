package com.shmet.handler.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.Ehang365StartStopBean;
import com.shmet.bean.Lock;
import com.shmet.dao.redis.ShorePowerRedisDao;

@Component
public class EHang365Listener {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Autowired
  ShorePowerRedisDao shorePowerRedisDao;
  
  // 用来存放对象锁
  private Map<String, Lock> lockMap = new ConcurrentHashMap<>();

  private String getKey (Long acconeId, Integer deviceId) {
    return acconeId.toString() + "@" + deviceId.toString();
  }
  public boolean putLockerMap(Long acconeId, Integer deviceId, Lock lock) {
    String key = getKey(acconeId, deviceId);
    Lock lockOld = this.lockMap.get(key);
    if (this.lockMap.containsKey(key) && lockOld != null) {
      return false;
    } else {
      this.lockMap.put(key, lock);
      return true;
    }
  }
  
  public void removeLockerMap(Long acconeId, Integer deviceId) {
    String key = getKey(acconeId, deviceId);
    this.lockMap.remove(key);
  }
  
  private Object getLockerMap(Long acconeId, Integer deviceId) {
    String key = getKey(acconeId, deviceId);
    return this.lockMap.get(key);
  }

  //@RabbitListener(queues = "${rabbitmq.queues.ehang365eqptstart.response}")
  public void listenEhang365eqptstart(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();
    Ehang365StartStopBean message = null;
    Object lock = null;
    try {
      message = mapper.readValue(new String(msg), Ehang365StartStopBean.class);
      if (message != null) {
        if (message.getDeviceId() == null) {
          // 江苏岸电deviceId全部为1
          message.setDeviceId(1);
        }
        lock = getLockerMap(message.getAcconeId(), message.getDeviceId());
        shorePowerRedisDao.saveStartInfoCache(message.getAcconeId(), message.getDeviceId(), message);
      }
    } catch (Exception e) {
      logger.error(Consts.MSG_CODE_E010000, e);
    } finally {
      try{
        if (lock != null) {
          synchronized (lock) {
            lock.notify();
          }
        }
      } catch (Throwable t) {
        logger.debug(t.getMessage(), t);
      }
      
    }
  }
  
  //@RabbitListener(queues = "${rabbitmq.queues.ehang365eqptstop.response}")
  public void listenEhang365eqptstop(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();
    Ehang365StartStopBean message = null;
    Object lock = null;
    try {
      message = mapper.readValue(new String(msg), Ehang365StartStopBean.class);
      if (message != null) {
        lock = getLockerMap(message.getAcconeId(), message.getDeviceId());
        //shorePowerRedisDao.saveStopInfoCache(message.getAcconeId(), message.getDeviceId(), message);
      }
    } catch (Exception e) {
      logger.error(Consts.MSG_CODE_E010000, e);
    } finally {
      try{
        if (lock != null) {
          synchronized (lock) {
            lock.notify();
          }
        }
      } catch (Throwable t) {
        logger.debug(t.getMessage(), t);
      }
      
    }
  }

}
