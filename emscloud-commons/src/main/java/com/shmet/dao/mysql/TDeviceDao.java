package com.shmet.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.entity.mysql.gen.TWaterMeterTask;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.Consts;
import com.shmet.dao.mysql.mapper.TDeviceMapperDao;
import com.shmet.entity.mysql.gen.TDevice;
import com.shmet.helper.redis.RedisUtil;

@Component
public class TDeviceDao {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SQLManager sqlManager;

    @Autowired
    TDeviceMapperDao tDeviceMapperDao;

    @Autowired
    private RedisUtil redisUtil;

    final String key = "TDeviceDao.findAllMappedByDevice";

    final String key_findByCondition = "TDeviceDao.findByCondition";

    public List<TDevice> findAll() {
        return tDeviceMapperDao.all();
    }

    /**
     * 根据岸电id模糊查询id
     * @param id
     * @return
     */
    public TDevice findByAnDianId(String id){
        Query<TDevice> query=  tDeviceMapperDao.createQuery();
        query.andLike("anDianId",id);
        String sql="select * from t_device where andianid like '%"+id+"%'";
        List<TDevice> list= tDeviceMapperDao.execute(sql);
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }else{
            return  null;
        }
    }

    public TDevice findByDeviceNo(Long deviceNo) {
        TDevice device = null;
        String deviceNoStr = deviceNo.toString();
        if (redisUtil.hHasKey(key, deviceNoStr)) {
            device = (TDevice) redisUtil.hget(key, deviceNoStr, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        } else {
            LambdaQuery<TDevice> query= tDeviceMapperDao.createLambdaQuery();
            query.andEq(TDevice::getDeviceNo,deviceNo);
            device= query.single();
            if(device!=null)
            redisUtil.hset(key, deviceNoStr, device, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);

//            TDevice tDevice=new TDevice();
//            tDevice.setDeviceNo(deviceNo);
//            List<TDevice> deviceList = tDeviceMapperDao.selectByCondition(tDevice);
//            if (deviceList != null && deviceList.size() > 0) {
//                redisUtil.hset(key, deviceList.get(0).getDeviceNo().toString(), deviceList.get(0), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
//            } else {
//                return null;
//            }

        }
        return device;
    }

    public TDevice findByCondition(TDevice device) {
        String keytem = device.getAcconeId() + //"." + device.getDeviceModel() +
                "." + device.getDeviceId();
        if (!redisUtil.hHasKey(key_findByCondition, keytem)) {
            List<TDevice> deviceList = tDeviceMapperDao.selectByCondition(device);
            if (deviceList == null || deviceList.size() == 0) {
                return null;
            }
            if (deviceList.size() > 1) {
                logger.warn(Consts.MSG_CODE_W000005);
            }
            redisUtil.hset(key_findByCondition, keytem, deviceList.get(0));
        }
        return (TDevice) redisUtil.hget(key_findByCondition, keytem, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

    public void saveAll(List<TDevice> deviceList) {
        List<TDevice> notExists = new ArrayList<>();
        for (TDevice device : deviceList) {
            if (redisUtil.hHasKey(key, device.getDeviceNo().toString())) {
                tDeviceMapperDao.updateById(device);
            } else {
                notExists.add(device);
                redisUtil.hset(key, device.getDeviceNo().toString(), device, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
            }
        }
        if (notExists.size() > 0) {
            tDeviceMapperDao.insertBatch(notExists);
        }
    }
}
