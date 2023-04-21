package com.shmet.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.shmet.bean.RealDataItem;
import com.shmet.dao.redis.RealDataRedisDao;

@Repository
public class BaishuDataProvideDao {

    @Autowired
    RealDataRedisDao redisCacheDao;

    /**
     * 获取设备点位实际数据
     *
     * @param deviceNo 设备编号
     * @param tagCode  点位编码
     */
    public RealDataItem queryRealData(Long deviceNo, String tagCode) {

        Object realData = redisCacheDao.getRealData(deviceNo, tagCode);

        RealDataItem realDataItem;

        if (realData != null) {
            realDataItem = (RealDataItem) realData;

            String dateTime = realDataItem.getDateTime();
            String year = dateTime.substring(0, 4);
            String month = dateTime.substring(4, 6);
            String day = dateTime.substring(6, 8);
            String hour = dateTime.substring(8, 10);
            String minute = dateTime.substring(10, 12);
            String second = dateTime.substring(12, 14);
            String collTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

            realDataItem.setDateTime(collTime);
            return realDataItem;
        } else {
            return null;
        }

    }

}
