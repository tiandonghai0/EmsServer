package com.shmet.dao.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.entity.mongo.GroupDeviceTagItem;
import com.shmet.entity.mongo.GroupTagConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class GroupTagConfigDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;

    final String key_deviceNo = "GroupTagConfigDao.findByDeviceNo.deviceNo";
    final String key_groupNo = "GroupTagConfigDao.findByDeviceNo.groupNo";

    public List<GroupTagConfig> findAll() {
        List<GroupTagConfig> rst = null;
        rst = mongoTemplate.findAll(GroupTagConfig.class);
        return rst;
    }

    public List<GroupTagConfig> findByDeviceNo(long deviceNo) {
        String deviceNoStr = String.valueOf(deviceNo);
        List<GroupTagConfig> rst = null;
        List<Long> deviceNoList = new ArrayList<>();
        deviceNoList.add(deviceNo);
        if (redisUtil.hHasKey(key_deviceNo, deviceNoStr)) {
            rst = (List<GroupTagConfig>) redisUtil.hget(key_deviceNo, deviceNoStr, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        } else {
            Query query =
                    Query.query(Criteria.where("deviceNoTagCodeList.deviceNo").in(deviceNoList));
            rst = mongoTemplate.find(query, GroupTagConfig.class);
            redisUtil.hset(key_deviceNo, deviceNoStr, rst, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return rst;
    }

    public void saveAll(List<GroupTagConfig> list) {
        List<GroupTagConfig> notExists = new ArrayList<GroupTagConfig>();
        for (GroupTagConfig group : list) {
            String itemKey = String.valueOf(group.getGroupNo());
            if (redisUtil.hHasKey(key_groupNo, itemKey)) {
                mongoTemplate.save(group);
            } else {
                notExists.add(group);
            }
            if (redisUtil.hHasKey(key_groupNo, itemKey)) {
                redisUtil.hdel(key_groupNo, group.getGroupNo());
            }
            ;
            redisUtil.hset(key_groupNo, itemKey, group, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);

            List<GroupDeviceTagItem> groupDeviceTagItemList = group.getDeviceNoTagCodeList();
            if (groupDeviceTagItemList != null && groupDeviceTagItemList.size() > 0) {
                for (GroupDeviceTagItem groupDeviceTagItem : groupDeviceTagItemList) {
                    String deviceNo = String.valueOf(groupDeviceTagItem.getDeviceNo());
                    List<GroupTagConfig> groupList = new ArrayList<>();
                    if (redisUtil.hHasKey(key_deviceNo, deviceNo)) {
                        List<GroupTagConfig> groupListTemp = (List<GroupTagConfig>) redisUtil.hget(key_deviceNo, deviceNo, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
                        for (GroupTagConfig groupTagConfig : groupListTemp) {
                            if (groupTagConfig.getGroupNo() != group.getGroupNo()) {
                                groupList.add(groupTagConfig);
                            }
                        }
                        redisUtil.hdel(key_deviceNo, deviceNo);
                    }
                    groupList.add(group);
                    redisUtil.hset(key_deviceNo, deviceNo, groupList, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
                }
            }
        }
        if (notExists.size() > 0) {
            mongoTemplate.insertAll(notExists);
        }
    }

}
