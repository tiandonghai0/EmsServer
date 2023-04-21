package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.ChargePolicyCustom;

@Repository
public class ChargePolicyCustomDao {
  @Autowired
  private MongoTemplate mongoTemplate;

  public ChargePolicyCustom findBySubProjectId(Long subProjectId, Integer day) {
    Query query = Query.query(Criteria.where("subProjectId").is(subProjectId).and("dayFrom")
        .lte(day).and("dayTo").gte(day));
    return mongoTemplate.findOne(query, ChargePolicyCustom.class);
  }
}
