package com.shmet.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.mongo.ProjectConfig;

@Service
public class BaishuElectricPricePeriodHandler {
    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    public JSONObject findProjectConfig() {

        ProjectConfig projectConfig = projectConfigDao.findProjectConfigBySubProjectIdNoCache(20180001001L);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("rows", projectConfig);

        return jsonObject;
    }

    public boolean updateProjectConfig(ProjectConfig projectConfig) {
        Query query = Query.query(Criteria.where("subProjectId").is(20180001001L));
        Update update = new Update();
        update.set("electricPricePeriod", projectConfig.getElectricPricePeriod());
        UpdateResult updateFirst = this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
        return true;
    }

}
