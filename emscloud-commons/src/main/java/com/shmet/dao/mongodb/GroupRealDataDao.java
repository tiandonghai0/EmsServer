package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.GroupRealData;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class GroupRealDataDao extends BatchUpdateDao {

    private final String findOneByGroupHour_key = "GroupRealDataDao.findOneByGroupHour";

    private final int key_time = 5 * 60;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void upsertStatistics(Update update, GroupRealData groupRealData) {
        String innerkey = groupRealData.getGroupNo() + "." + groupRealData.getHour();
        mongoTemplate.upsert(Query.query(Criteria.where("groupNo")
                .is(groupRealData.getGroupNo()).and("hour").is(groupRealData.getHour())), update, GroupRealData.class);
        redisUtil.hset(findOneByGroupHour_key, innerkey, groupRealData, key_time);
    }

    public GroupRealData findOneByGroupHour(long groupNo, int hour) {
        String innerkey = groupNo + "." + hour;
        if (redisUtil.hHasKey(findOneByGroupHour_key, innerkey)) {
            return (GroupRealData) redisUtil.hget(findOneByGroupHour_key, innerkey, key_time);
        } else {
            Query query =
                    Query.query(Criteria.where("groupNo").is(groupNo).and("hour").is(hour));
            GroupRealData groupRealData = mongoTemplate.findOne(query, GroupRealData.class);
            if (groupRealData != null) {
                redisUtil.hset(findOneByGroupHour_key, innerkey, groupRealData, key_time);
            }
            return groupRealData;
        }

    }
}
