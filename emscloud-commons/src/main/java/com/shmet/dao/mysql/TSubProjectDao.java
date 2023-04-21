package com.shmet.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.Consts;
import com.shmet.dao.mysql.mapper.TSubProjectMapperDao;
import com.shmet.entity.mysql.gen.TSubProject;
import com.shmet.helper.redis.RedisUtil;

import javax.annotation.Resource;

@Component
public class TSubProjectDao {
  @Autowired
  SQLManager sqlManager;

  @Resource
  TSubProjectMapperDao tSubProjectMapperDao;

  @Autowired
  private RedisUtil redisUtil;

  final String key = "TSubProjectDao.findBySubProjectId";

  public TSubProject findById(Long subProjectId) {
    if (subProjectId == null) {
      return null;
    }
    if (!redisUtil.hHasKey(key, subProjectId.toString())) {
      TSubProject subProject = new TSubProject();
      subProject.setSubProjectId(subProjectId);
      List<TSubProject> subProjectList = tSubProjectMapperDao.selectByCondition(subProject);
      if (subProjectList == null || subProjectList.size() == 0) {
        return null;
      }
      redisUtil.hset(key, subProjectId.toString(), subProjectList.get(0), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }
    return (TSubProject) redisUtil.hget(key, subProjectId.toString(), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
  }

  public void saveAll(List<TSubProject> subProjectList) {
    List<TSubProject> notExists = new ArrayList<>();
    for (TSubProject subProject : subProjectList) {
      if (redisUtil.hHasKey(key, subProject.getSubProjectId().toString())) {
        tSubProjectMapperDao.updateById(subProject);
      } else {
        notExists.add(subProject);
        redisUtil.hset(key, subProject.getSubProjectId().toString(), subProject, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
      }
    }
    if (notExists.size() > 0) {
      tSubProjectMapperDao.insertBatch(notExists);
    }
  }
}
