package com.shmet.dao.mongodb;

import com.shmet.entity.mongo.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.ShorePowerLogData;

import java.util.List;

@Repository
public class ShorePowerLogDataDao extends BatchUpdateDao {

  @Autowired
  private MongoTemplate mongoTemplate;

  public void save(ShorePowerLogData logData) {
    mongoTemplate.save(logData);
  }

  public ShorePowerLogData findLastByAcconeDevice(Long acconeId, Integer deviceId) {
    ShorePowerLogData rst;
    Query query =
        Query.query(Criteria.where("acconeId").is(acconeId).and("deviceId").is(deviceId));
    query.with(Sort.by(new Order(Direction.DESC, "timestamp")));
    rst = mongoTemplate.findOne(query, ShorePowerLogData.class);
    return rst;
  }

  public void update(Long acconeId, Integer deviceId,String orderId,String powerOutletId){

    Query query =
        Query.query(Criteria.where("acconeId").is(acconeId).and("deviceId").is(deviceId));
    query.with(Sort.by(new Order(Direction.DESC, "timestamp")));
    Update update=new Update();
    update.set("orderId", orderId);
    update.set("powerOutletId", powerOutletId);
    this.mongoTemplate.updateFirst(query, update, ShorePowerLogData.class);

  }

  public void updateModel(ShorePowerLogData shorePowerLogData){

    Query query =
        Query.query(Criteria.where("_id").is(shorePowerLogData.getId()));
    //query.with(Sort.by(new Order(Direction.DESC, "timestamp")));
    Update update=new Update();
    update.set("orderId", shorePowerLogData.getOrderId());
    update.set("powerOutletId", shorePowerLogData.getPowerOutletId());
    update.set("ssType", shorePowerLogData.getSsType());
    update.set("currentEnergy", shorePowerLogData.getCurrentEnergy());
    update.set("startEnergy", shorePowerLogData.getStartEnergy());
    update.set("diff", shorePowerLogData.getDiff());
    update.set("dateTime", shorePowerLogData.getDateTime());
    update.set("timestamp", shorePowerLogData.getTimestamp());
    update.set("startTime", shorePowerLogData.getStartTime());
    this.mongoTemplate.updateFirst(query, update, ShorePowerLogData.class);

  }

  public void updateSsType(ShorePowerLogData shorePowerLogData){

    Query query =
        Query.query(Criteria.where("_id").is(shorePowerLogData.getId()));
    //query.with(Sort.by(new Order(Direction.DESC, "timestamp")));
    Update update=new Update();
    update.set("ssType", shorePowerLogData.getSsType());
    update.set("diff",shorePowerLogData.getDiff());
    this.mongoTemplate.updateFirst(query, update, ShorePowerLogData.class);

  }

  public List<ShorePowerLogData> getUseElec(){
    Query query =
        Query.query(Criteria.where("ssType").is(1));
    query.with(Sort.by(new Order(Direction.DESC, "timestamp")));
    return mongoTemplate.find(query,ShorePowerLogData.class);
  }

}
