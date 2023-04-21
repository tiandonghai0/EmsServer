package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.ScriptConfig;

@Repository
public class ScriptConfigDao extends BatchUpdateDao {

    @Autowired
    private MongoTemplate mongoTemplate;
  
    public ScriptConfig findByProjectScriptGateway(Long subProjectId, Long scriptId, Integer gatewayId) {
        Query query = Query.query(Criteria.where("subProjectId").is(subProjectId).and("scriptId").is(scriptId).and("gatewayId").is(gatewayId));
        return mongoTemplate.findOne(query, ScriptConfig.class);
    }

    public void save(ScriptConfig data) {
        mongoTemplate.save(data);
    }
}
