package com.shmet.dao.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.DummyDeviceConfig;

@Repository
public class DummyDeviceConfigDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<DummyDeviceConfig> findByProject(Long subProjectNo) {
        Query query = Query.query(Criteria.where("subProjectId").is(subProjectNo));
        return mongoTemplate.find(query, DummyDeviceConfig.class);
    }

    public List<DummyDeviceConfig> findAll() {
        return mongoTemplate.findAll(DummyDeviceConfig.class);
    }

    public void save(DummyDeviceConfig config) {
        mongoTemplate.save(config);
    }

}
