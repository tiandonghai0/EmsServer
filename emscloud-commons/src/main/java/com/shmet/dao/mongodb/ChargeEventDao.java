package com.shmet.dao.mongodb;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.shmet.DateTimeUtils;
import com.shmet.bean.ChargeDisChargeBean;
import com.shmet.entity.mongo.ChargeEvent;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class ChargeEventDao extends BatchUpdateDao {

    final String key = "ChargeEventDao.findLastEventByProjectDevice";

    final long timeout = 1800;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;

    public ChargeEvent save(ChargeEvent chargeEvent) {
        ChargeEvent rst = mongoTemplate.save(chargeEvent);
        redisUtil.hset(key, String.valueOf(chargeEvent.getAcconeId()), rst, timeout);
        return rst;
    }

    public void updateForConfirm(ChargeDisChargeBean chargeDisCahrgeBean) {
        if (chargeDisCahrgeBean == null) {
            return;
        }
        Long curTime = Long.valueOf(DateTimeUtils.date2String(new Date(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS));
        Update update = new Update();
        Query query = Query.query(Criteria.where("_id")
                .is(chargeDisCahrgeBean.getEventId()));
        update.set("isConfirmed", true);
        update.set("confirmTime", curTime);
        mongoTemplate.updateMulti(query, update, ChargeEvent.class);
        String itemKey = String.valueOf(chargeDisCahrgeBean.getAcconeId());
        ChargeEvent chargeEvent = findEventById(chargeDisCahrgeBean.getEventId());
        redisUtil.hset(key, itemKey, chargeEvent, timeout);
    }

    public ChargeEvent findEventById(String eventId) {
        return mongoTemplate.findById(eventId, ChargeEvent.class);
    }

    public ChargeEvent findLastEventByProjectDevice(Long subProjectId, Long acconeId) {
        ChargeEvent rst;
        String itemKey = String.valueOf(acconeId);
        if (redisUtil.hHasKey(key, itemKey)) {
            rst = (ChargeEvent) redisUtil.hget(key, itemKey, timeout);
        } else {
            Query query =
                    Query.query(Criteria.where("subProjectId").is(subProjectId).and("acconeId").is(acconeId));
            query.with(Sort.by(new Order(Direction.DESC, "sendTime")));
            rst = mongoTemplate.findOne(query, ChargeEvent.class);
            redisUtil.hset(key, itemKey, rst, timeout);
        }
        return rst;
    }

    public ChargeEvent findLastCommandEventByProjectDevice(Long subProjectId, Long acconeId,Integer command) {
        ChargeEvent rst;
        String itemKey = String.valueOf(acconeId);
        if (redisUtil.hHasKey(key, itemKey)) {
            rst = (ChargeEvent) redisUtil.hget(key, itemKey, timeout);
        } else {
            Query query =
                Query.query(Criteria.where("subProjectId").is(subProjectId).and("acconeId").is(acconeId).and("chargeType").is(command));
            query.with(Sort.by(new Order(Direction.DESC, "sendTime")));
            rst = mongoTemplate.findOne(query, ChargeEvent.class);
            redisUtil.hset(key, itemKey, rst, timeout);
        }
        return rst;
    }

    public ChargeEvent findLastEventByProjectDevice(Long subProjectId, Long acconeId,Integer esNo) {
        ChargeEvent rst;
        String itemKey = String.valueOf(acconeId);
        if (redisUtil.hHasKey(key, itemKey)) {
            rst = (ChargeEvent) redisUtil.hget(key, itemKey, timeout);
        } else {
            Query query =
                    Query.query(Criteria.where("subProjectId").is(subProjectId).and("acconeId").is(acconeId).and("esNo").is(esNo));

            query.with(Sort.by(new Order(Direction.DESC, "sendTime")));
            rst = mongoTemplate.findOne(query, ChargeEvent.class);
            redisUtil.hset(key, itemKey, rst, timeout);
        }
        return rst;
    }

    public List<ChargeEvent> findLastRowsEventByProjectDevice(Long subProjectId, Long acconeId, Integer esNo, Integer rows) {
        try {
            List<ChargeEvent> rst;
            Query query =
                Query.query(Criteria.where("subProjectId").is(subProjectId).and("acconeId").is(acconeId).and("esNo").is(esNo));
            query.limit(3);
            query.with(Sort.by(new Order(Direction.DESC, "sendTime")));

            rst = mongoTemplate.find(query, ChargeEvent.class);

            return rst;
        }catch (Exception e){
            return null;
        }
    }
}
