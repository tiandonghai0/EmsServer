package com.shmet.dao.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.entity.mongo.AcconeAlarmData;
import com.shmet.helper.redis.RedisUtil;

@Component
public class ErrorReportRedisDao {
    @Autowired
    private RedisUtil redisUtil;

    final String key_alarmdata = "ErrorReportRedisDao.AcconeAlarm";

    final long timeout_msgdata = -1;

    public void saveAcconeAlarmDataCache(Long acconeId, Long deviceNo, List<AcconeAlarmData> value) {
        redisUtil.hset(key_alarmdata, getInnerKey(acconeId, deviceNo), value, timeout_msgdata);
    }

    public Object getAcconeAlarmDataCache(Long acconeId, Long deviceNo) {
        return redisUtil.hget(key_alarmdata, getInnerKey(acconeId, deviceNo), timeout_msgdata);
    }

    private String getInnerKey(Long acconeId, Long deviceNo) {
        return acconeId + "." + deviceNo;
    }
}
