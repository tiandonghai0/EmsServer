package com.shmet.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.LogData;

@Repository
public class LogDataDao extends BatchUpdateDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(LogData logData) {
        mongoTemplate.save(logData);
    }
}
