package com.shmet.dao.mongodb;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import com.shmet.helper.mongo.MongoBathUpdateOptions;
import com.shmet.helper.mongo.MongoHelper;

@Repository
public class BatchUpdateDao {
  @Autowired
  private MongoTemplate mongoTemplate;
  
  public void batchUpdateByOptions(Class<?> c, List<MongoBathUpdateOptions> mongoBathUpdateOptionsList) {
    MongoHelper.bathUpdate(mongoTemplate, c, mongoBathUpdateOptionsList);
  }
}
