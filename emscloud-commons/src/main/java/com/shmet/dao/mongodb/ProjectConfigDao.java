package com.shmet.dao.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.shmet.Consts;
import com.shmet.DateTimeUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.mongo.DeviceNoTagCodeItem;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.helper.redis.RedisUtil;

@Repository
public class ProjectConfigDao {
  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private RealDataRedisDao realDataRedisDao;

  String key = "ProjectConfigDao.findBySubProjectId";

  String key2 = "ProjectConfigDao.findElectricProjectConfigBySubProjectId";

  public Object findBySubProjectId(Long subProjectId, String configKey) {
    String itemKey = subProjectId + Consts.KEY_SEP + configKey;
    Object rst = null;
    if (!redisUtil.hHasKey(key, itemKey)) {
      ProjectConfig data = null;
      Query query = Query.query(Criteria.where("subProjectId").is(subProjectId));
      data = mongoTemplate.findOne(query, ProjectConfig.class);
      if (data != null) {
        if (data.getConfig().containsKey(configKey)) {
          rst = data.getConfig().get(configKey);
          redisUtil.hset(key, itemKey, rst, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
      }
    } else {
      rst = redisUtil.hget(key, itemKey, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }
    return rst;
  }

  public ProjectConfig findProjectConfigBySubProjectIdNoCache(Long subProjectId) {
    Query query = Query.query(Criteria.where("subProjectId").is(subProjectId));
    return mongoTemplate.findOne(query, ProjectConfig.class);
  }

  public ProjectConfig findProjectConfigByProjectIdNoCache(Long projectId) {
    Query query = Query.query(Criteria.where("projectId").is(projectId));
    return mongoTemplate.findOne(query, ProjectConfig.class);
  }

  public List<ProjectConfig> findAllElectricPricePeriod() {
    Query query = Query.query(Criteria.where("electricPricePeriod").exists(true));
    return mongoTemplate.find(query, ProjectConfig.class);
  }

  public ProjectConfig findElectricProjectConfigBySubProjectId(Long subProjectId) {
    String itemKey = String.valueOf(subProjectId);
    ProjectConfig config = null;
    if (!redisUtil.hHasKey(key2, itemKey)) {
      Query query = Query.query(Criteria.where("electricPricePeriod").exists(true).and("subProjectId").is(subProjectId));
      config = mongoTemplate.findOne(query, ProjectConfig.class);
      if (config == null) {
        config = new ProjectConfig();
      }
      redisUtil.hset(key2, itemKey, config, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }
    config = (ProjectConfig) redisUtil.hget(key2, itemKey, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    if (config.getElectricPricePeriod() == null) {
      config = null;
    }
    return config;
  }

  public ProjectConfig findElectricProjectConfigBySubProjectIdNoCache(Long subProjectId) {
    String itemKey = String.valueOf(subProjectId);
    ProjectConfig config = null;
    Query query = Query.query(Criteria.where("electricPricePeriod").exists(true).and("subProjectId").is(subProjectId));
    config = mongoTemplate.findOne(query, ProjectConfig.class);
    if (config == null) {
      config = new ProjectConfig();
    }
    redisUtil.hset(key2, itemKey, config, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    return config;
  }

  public List<ProjectConfig> findElectricProjectConfigBySubProjectIdsNoCache(List<Long> subProjectIds) {
    List<ProjectConfig> projectConfigs = new ArrayList<>();
    subProjectIds.forEach(o -> {
      ProjectConfig config = findElectricProjectConfigBySubProjectIdNoCache(o);
      projectConfigs.add(config);
    });
    return projectConfigs;
  }

  public Long findAll(){
    Query query = Query.query(Criteria.where("_id").exists(true));
    return  mongoTemplate.count(query,ProjectConfig.class);
  }

  public ProjectConfig save(ProjectConfig projectConfig) {
    if (projectConfig.getId() == null || projectConfig.getId().equals("")) {
      return null;
    }
    ProjectConfig rst = mongoTemplate.save(projectConfig);
    String itemKey = String.valueOf(rst.getSubProjectId());
    redisUtil.hset(key2, itemKey, rst, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    return rst;
  }

  /**
   * 保存 projectConfig 到缓存
   */
  public ProjectConfig SaveProjectConfigCache(ProjectConfig projectConfig) {
    String itemKey = String.valueOf(projectConfig.getSubProjectId());
    redisUtil.hset(key2, itemKey, projectConfig, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    return projectConfig;
  }

  /**
   * 删除缓存
   */
  public void delProjectConfigCache(Long subProjectId){
    redisUtil.hdel(key2,subProjectId.toString());
  }

  public double getLastMinSoc(List<DeviceNoTagCodeItem> deviceNoTagCodeItemSocList) {
    double minSoc = -1;
    for (DeviceNoTagCodeItem deviceNoTagCodeItemSoc : deviceNoTagCodeItemSocList) {
      // 获取最近的soc数据
      RealDataItem realDataItemSoc =
          (RealDataItem) realDataRedisDao.getRealData(deviceNoTagCodeItemSoc.getDeviceNo(),
              deviceNoTagCodeItemSoc.getTagCode());
      if (realDataItemSoc != null) {
        Date realDate =
            DateTimeUtils.parseDate(realDataItemSoc.getDateTime(),
                DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
        long second = DateTimeUtils.differFromSecond(realDate, new Date());
        // 6分钟之内的值均可信
        if (second < 360) {
          double curSoc = Double.parseDouble(realDataItemSoc.getData().toString());
          if (minSoc == -1 || minSoc > curSoc) {
            minSoc = curSoc;
          }
        }
      }
    }
    return minSoc;
  }

  public double getLastMaxSoc(List<DeviceNoTagCodeItem> deviceNoTagCodeItemSocList) {
    double maxSoc = -1;
    for (DeviceNoTagCodeItem deviceNoTagCodeItemSoc : deviceNoTagCodeItemSocList) {
      // 获取最近的soc数据
      RealDataItem realDataItemSoc =
          (RealDataItem) realDataRedisDao.getRealData(deviceNoTagCodeItemSoc.getDeviceNo(),
              deviceNoTagCodeItemSoc.getTagCode());
      if (realDataItemSoc != null) {
        Date realDate =
            DateTimeUtils.parseDate(realDataItemSoc.getDateTime(),
                DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
        long second = DateTimeUtils.differFromSecond(realDate, new Date());
        // 6分钟之内的值均可信
        if (second < 360) {
          double curSoc = Double.parseDouble(realDataItemSoc.getData().toString());
          if (maxSoc == -1 || maxSoc < curSoc) {
            maxSoc = curSoc;
          }
        }
      }
    }
    return maxSoc;
  }

  public double getLastSoc(int chargeType, ProjectConfig projectConfig) {
    List<DeviceNoTagCodeItem> deviceNoTagCodeItemSocList = projectConfig.getDeviceNoTagCodeListToCheckSoc();
    if (deviceNoTagCodeItemSocList == null || deviceNoTagCodeItemSocList.size() == 0) {
      return -1;
    }
    if (chargeType == Consts.ELECTRIC_CHARGE) {
      return getLastMaxSoc(deviceNoTagCodeItemSocList);
    } else {
      return getLastMinSoc(deviceNoTagCodeItemSocList);
    }
  }

  public double getLastMaxT(ProjectConfig projectConfig) {
    List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckMaxT = projectConfig.getDeviceNoTagCodeListToCheckMaxT();
    if (deviceNoTagCodeListToCheckMaxT == null || deviceNoTagCodeListToCheckMaxT.size() == 0) {
      return -1;
    }
    return getLastMaxSoc(deviceNoTagCodeListToCheckMaxT);

  }

  /**
   * 获取电池最高温度
   */
  public double getLastMaxT(List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckMaxT) {
    double maxT = -1;
    for (DeviceNoTagCodeItem deviceNoTagCodeItemMaxT : deviceNoTagCodeListToCheckMaxT) {
      // 获取最近的soc数据
      RealDataItem realDataItemMaxT =
          (RealDataItem) realDataRedisDao.getRealData(deviceNoTagCodeItemMaxT.getDeviceNo(),
              deviceNoTagCodeItemMaxT.getTagCode());
      if (realDataItemMaxT != null) {
        Date realDate =
            DateTimeUtils.parseDate(realDataItemMaxT.getDateTime(),
                DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
        long second = DateTimeUtils.differFromSecond(realDate, new Date());
        // 6分钟之内的值均可信
        if (second < 360) {
          double curMaxT = Double.parseDouble(realDataItemMaxT.getData().toString());
          if (maxT == -1 || maxT < curMaxT) {
            maxT = curMaxT;
          }
        }
      }
    }
    return maxT;
  }

}
