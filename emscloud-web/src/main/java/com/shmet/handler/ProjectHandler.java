package com.shmet.handler;

import com.shmet.DateTimeUtils;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.mongo.DeviceNoTagCodeItem;
import com.shmet.entity.mongo.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectHandler {
  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * 根据 subProjectId Mongo 查询 ProjectConfig
   *
   * @param subProjectId subProjectId
   * @return ProjectConfig
   */
  public ProjectConfig getProjectConfigBySubProjectId(Long subProjectId) {
    return projectConfigDao.findProjectConfigBySubProjectIdNoCache(subProjectId);
  }

  /**
   * 更新 projectConfig.electricPricePeriod 储能策略
   *
   * @param projectConfig ProjectConfig
   * @return boolean
   */
  public boolean updateProjectConfigBySubProjectId(ProjectConfig projectConfig) {
    Query query = Query.query(Criteria.where("subProjectId").is(projectConfig.getSubProjectId()));
    Update update = new Update();
    update.set("electricPricePeriod", projectConfig.getElectricPricePeriod());
    update.set("essAMStatus", projectConfig.getEssAMStatus());
    Map<String, Object> config = projectConfig.getConfig();
    if (config != null) {
      update.set("config", config);
    }
    List<DeviceNoTagCodeItem> deviceNoTagCodeListToCalcPrice = projectConfig.getDeviceNoTagCodeListToCalcPrice();
    if (deviceNoTagCodeListToCalcPrice != null) {
      update.set("deviceNoTagCodeListToCalcPrice", deviceNoTagCodeListToCalcPrice);
    }
    update.set("strategySaveTime", DateTimeUtils.getCurrentLongTime());
    this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
    //重新缓存 ProjectConfig
    projectConfigDao.SaveProjectConfigCache(projectConfig);
    return true;
  }

  /**
   * 更新 projectConfig.airConditionerPeriod 空调策略
   *
   * @param projectConfig ProjectConfig
   * @return boolean
   */
  public boolean updateProjectConfigAirConditionerPeriodBySubProjectId(ProjectConfig projectConfig) {
    Query query = Query.query(Criteria.where("subProjectId").is(projectConfig.getSubProjectId()));
    Update update = new Update();
    update.set("airConditionerPeriod", projectConfig.getAirConditionerPeriod());

    this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
    //重新缓存 ProjectConfig
    projectConfigDao.SaveProjectConfigCache(projectConfig);
    return true;
  }

  /**
   * 更新 projectConfig.airConditionerPeriod 空调策略
   *
   * @param projectConfig ProjectConfig
   * @return boolean
   */
  public boolean updateProjectConfigAirBySubProjectId(ProjectConfig projectConfig) {
    Query query = Query.query(Criteria.where("subProjectId").is(projectConfig.getSubProjectId()));
    Update update = new Update();
    update.set("air", projectConfig.getAir());

    this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
    //重新缓存 ProjectConfig
    projectConfigDao.SaveProjectConfigCache(projectConfig);
    return true;
  }


  /**
   * 更新并离网状态标志
   *
   * @param projectConfig projectConfig
   */
  public void updateMergeLeaveStatus(ProjectConfig projectConfig, int status) {
    Query query = Query.query(Criteria.where("subProjectId").is(projectConfig.getSubProjectId()));
    Update update = new Update();

    update.set("config.onOffNetworkStatus", status);
    this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
  }

  /**
   * 更新 projectConfig.config 配置
   *
   * @param projectConfig projectConfig
   * @return boolean
   */
  public boolean updateProjectConfigConfigBySubProjectId(ProjectConfig projectConfig) {
    Query query = Query.query(Criteria.where("subProjectId").is(projectConfig.getSubProjectId()));
    Update update = new Update();
    update.set("config", projectConfig.getConfig());
    this.mongoTemplate.updateFirst(query, update, ProjectConfig.class);
    //重新缓存 ProjectConfig
    projectConfigDao.SaveProjectConfigCache(projectConfig);
    return true;
  }

}
