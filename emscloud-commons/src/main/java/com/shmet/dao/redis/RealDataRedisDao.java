package com.shmet.dao.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.bean.StatisticsDataItem;
import com.shmet.helper.redis.RedisUtil;

@Component
public class RealDataRedisDao {
    @Autowired
    private RedisUtil redisUtil;

    final String key_realdata = "RealDataRedisDao.saveRealDataCache";
    final String key_realdata_checkfault = "RealDataRedisDao.RealDataCacheCheckFault";
    final String key_devicemodeltagcode = "RealDataRedisDao.saveDeviceModelTagCodesCache";
    final String key_hour_statisticsdata = "RealDataRedisDao.saveHourStatisticsDataCache";

    final String key_teld = "RealDataRedisDao.TeldCache";
    final String key_langxin = "RealDataRedisDao.LangxinCache";
    final String key_langxin_cookies = "RealDataRedisDao.LangxinCookiesCache";

    // cache中的数据一直保存
    final long timeout_realdata = 0;

    final long timeout_realdata_checkfault = 2400;

    final long timeout_statisticsdata = 300;

    final long timeout_teld = 300;

    final long timeout_langxin = (60 * 60 * 24 * 25);//25天，对方有效期是30天

    final long timeout_langxin_cookies = 3600;

    public void saveRealDataCache(Long deviceNo, String tagCode, Object value) {
        redisUtil.hset(key_realdata, getInnerKey(deviceNo, tagCode), value, timeout_realdata);
    }

    public Object getRealData(Long deviceNo, String tagCode) {
        return redisUtil.hget(key_realdata, getInnerKey(deviceNo, tagCode), timeout_realdata);
    }

    public void saveDeviceModelTagCodesCache(String accOneId, String deviceModel, List<String> value) {
        redisUtil.hset(key_devicemodeltagcode, getInnerKey(accOneId, deviceModel), value, timeout_realdata);
    }

    public Object getDeviceModelTagCodes(String accOneId, String deviceModel) {
        return redisUtil.hget(key_realdata, getInnerKey(accOneId, deviceModel), timeout_realdata);
    }

    public void saveHourStatisticsDataCache(Long deviceNo, String tagCode, StatisticsDataItem value) {
        redisUtil.hset(key_hour_statisticsdata, getInnerKey(deviceNo, tagCode), value, timeout_statisticsdata);
    }

    public StatisticsDataItem getHourStatisticsDataCache(Long deviceNo, String tagCode) {
        Object obj = redisUtil.hget(key_hour_statisticsdata, getInnerKey(deviceNo, tagCode), timeout_statisticsdata);
        if (obj != null) {
            return (StatisticsDataItem) obj;
        } else {
            return null;
        }
    }

    public void saveTeldToken(String itemKey, Object value) {
        redisUtil.hset(key_teld, itemKey, value, timeout_teld);
    }

    public Object getTeldToken(String itemKey) {
        return redisUtil.hget(key_teld, itemKey, timeout_teld);
    }

    public void saveLangxinToken(String itemKey, Object value) {
        //redisUtil.hset(key_langxin, itemKey, value, timeout_langxin,);
        redisUtil.set(key_langxin + itemKey, value, timeout_langxin);
    }

    public Object getLangxinToken(String itemKey) {
        long exp = redisUtil.getExpire(key_langxin + itemKey);
        //return redisUtil.hget(key_langxin, itemKey, timeout_langxin);
        return redisUtil.get(key_langxin + itemKey, exp);
    }

    public void saveLangxinCookies(String itemKey, Object value) {
        redisUtil.set(key_langxin_cookies + itemKey, value, timeout_langxin_cookies);
    }

    public Object getLangxinCookies(String itemKey) {
        long exp = redisUtil.getExpire(key_langxin_cookies + itemKey);
        return redisUtil.get(key_langxin_cookies + itemKey, exp);
    }

    public void saveRealDataForCheckFault(Long deviceNo, String tagCode, Object value) {
        redisUtil.hset(key_realdata_checkfault, getInnerKey(deviceNo, tagCode), value, timeout_realdata_checkfault);
    }

    public Object getRealDataForCheckFault(Long deviceNo, String tagCode) {
        return redisUtil.hget(key_realdata_checkfault, getInnerKey(deviceNo, tagCode), timeout_realdata_checkfault);
    }

    public double getPrevP(String key) {
        long exp = redisUtil.getExpire("prevP" + key);
        Object p = redisUtil.get("prevP" + key, exp);
        if (p == null)
            return 0d;
        else
            return (double) p;
    }

    public void setPrevP(String key, double value) {
        redisUtil.set("prevP" + key, value);
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
