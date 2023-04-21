package com.shmet.dao.mongodb;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.entity.mongo.DeviceTagConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class DeviceTagConfigDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;

    String key = "DeviceTagConfigDao.findAllGroupByDeviceModel";

    String key_devicemodel_tagcode = "DeviceTagConfigDao.findDeviceTagConfig";

    String key_devicemodel_exists = "DeviceTagConfigDao.checkExistByDeviceModel";

    String key_devicemodel_needcompute = "DeviceTagConfigDao.findNeedComputeTagsByDeviceModel";

    public void upsertUnConfigDataByTagCode(DeviceTagConfig deviceTagConfig) {
        Update update = new Update();
        update.set("tagName", deviceTagConfig.getTagName());
        update.set("tagType", deviceTagConfig.getTagType());
        update.set("deviceModel", deviceTagConfig.getDeviceModel());
        mongoTemplate.upsert(Query.query(Criteria.where("tagCode")
                .is(deviceTagConfig.getTagCode()).and("deviceModel").is(deviceTagConfig.getDeviceModel())), update, DeviceTagConfig.class);
        DeviceTagConfig tempDeviceTagConfig = mongoTemplate.findOne(Query.query(Criteria.where("tagCode")
                .is(deviceTagConfig.getTagCode()).and("deviceModel").is(deviceTagConfig.getDeviceModel())), DeviceTagConfig.class);
        List<DeviceTagConfig> list = (List<DeviceTagConfig>) redisUtil.hget(key, deviceTagConfig.getDeviceModel(), -1);
        list.add(tempDeviceTagConfig);
        redisUtil.hdel(key, deviceTagConfig.getDeviceModel());
        redisUtil.hset(key, deviceTagConfig.getDeviceModel(), list);
    }

    public Map<String, List<DeviceTagConfig>> findAllGroupByDeviceModel() {
        List<DeviceTagConfig> allDeviceTagConfigList = mongoTemplate.findAll(DeviceTagConfig.class);
        if (allDeviceTagConfigList == null) {
            return null;
        }
        Map<String, List<DeviceTagConfig>> deviceTagConfigListMap =
                allDeviceTagConfigList.stream().collect(Collectors.groupingBy(DeviceTagConfig::getDeviceModel));
        return deviceTagConfigListMap;
    }

    public boolean checkExistByDeviceModel(String deviceModel) {
        boolean rst = false;
        String innerkey = key_devicemodel_exists;
        if (!redisUtil.hHasKey(innerkey, deviceModel)) {
            Query query = Query.query(Criteria.where("deviceModel").is(deviceModel));
            List<DeviceTagConfig> deviceTagConfigList = mongoTemplate.find(query, DeviceTagConfig.class);
            if (deviceTagConfigList != null && deviceTagConfigList.size() > 0) {
                redisUtil.hset(innerkey, deviceModel, true, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
                rst = true;
            }
        } else {
            rst = (boolean) redisUtil.hget(innerkey, deviceModel, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return rst;
    }

    public List<DeviceTagConfig> findNeedComputeTagsByDeviceModel(String deviceModel) {
        String innerkey = key_devicemodel_needcompute;
        List<DeviceTagConfig> deviceTagConfigList;
        if (!redisUtil.hHasKey(innerkey, deviceModel)) {
            Query query = Query.query(Criteria.where("deviceModel").is(deviceModel).and("needCompute").is(true));
            deviceTagConfigList = mongoTemplate.find(query, DeviceTagConfig.class);
            if (deviceTagConfigList != null && deviceTagConfigList.size() > 0) {
                redisUtil.hset(innerkey, deviceModel, deviceTagConfigList, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
            }
        } else {
            deviceTagConfigList = (List<DeviceTagConfig>) redisUtil.hget(innerkey, deviceModel, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return deviceTagConfigList;
    }

    public DeviceTagConfig findDeviceTagConfig(String deviceModel, String tagCode) {
        String innerkey = deviceModel + "." + tagCode;
        DeviceTagConfig data;
        if (!redisUtil.hHasKey(key_devicemodel_tagcode, innerkey)) {
            Query query = Query.query(Criteria.where("deviceModel").is(deviceModel).and("tagCode").is(tagCode));
            data = mongoTemplate.findOne(query, DeviceTagConfig.class);
            if (data != null) {
                redisUtil.hset(key_devicemodel_tagcode, innerkey, data, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
            }
        } else {
            data = (DeviceTagConfig) redisUtil.hget(key_devicemodel_tagcode, innerkey, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return data;
    }

    public List<DeviceTagConfig> findDeviceTagConfigByDeviceModel(String deviceModel) {
        List<DeviceTagConfig> data;
        Query query = Query.query(Criteria.where("deviceModel").is(deviceModel));
        data = mongoTemplate.find(query, DeviceTagConfig.class);
        return data;
    }

    public void save(DeviceTagConfig config) {
        mongoTemplate.save(config);
    }

}
