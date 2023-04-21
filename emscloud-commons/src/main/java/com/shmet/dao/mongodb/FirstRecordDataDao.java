package com.shmet.dao.mongodb;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.shmet.entity.mongo.FirstRecordData;

@Repository
public class FirstRecordDataDao extends BatchUpdateDao {

  @Autowired
  private MongoTemplate mongoTemplate;
  
  public List<FirstRecordData> findAllData() {
    return mongoTemplate.findAll(FirstRecordData.class);
  }
}
