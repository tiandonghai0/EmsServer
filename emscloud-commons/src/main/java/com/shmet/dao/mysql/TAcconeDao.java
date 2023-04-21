package com.shmet.dao.mysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.shmet.dao.mongodb.ProjectConfigDao;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.shmet.Consts;
import com.shmet.bean.AcconeStatusBean;
import com.shmet.dao.mysql.mapper.TAcconeMapperDao;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.helper.redis.RedisUtil;

import javax.annotation.Resource;

@Component
public class TAcconeDao {
    @Autowired
    SQLManager sqlManager;

    @Resource
    TAcconeMapperDao tAcconeMapperDao;

    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    private RedisUtil redisUtil;

    private String projectConfigKey = "ProjectConfigDao.findElectricProjectConfigBySubProjectId";

    final String key = "TAcconeDao.findAllUsingAcconeMappedByAcconeId";

    final String key_saveAcconeStatus = "TAcconeDao.saveAcconeStatus";

    public Map<String, TAccone> findAllUsingAcconeMappedByAcconeId() {
        if (!redisUtil.hasKey(key)) {
            TAccone accone = new TAccone();
            accone.setStatus(1);
            List<TAccone> acconeList = tAcconeMapperDao.selectByCondition(accone);
            if (acconeList == null) {
                return null;
            }
            Map<String, TAccone> map =
                    acconeList.stream().collect(
                            Collectors.toMap(k -> k.getAcconeId().toString(), d -> d, (t1, t2) -> t2,
                                    LinkedHashMap::new));
            redisUtil.hmset(key, map, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        return (Map<String, TAccone>) redisUtil.hmget(key, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

    public TAccone findAcconeByAcconeId(Long acconeId) {
        if (acconeId == null) {
            return null;
        }
        if (!redisUtil.hHasKey(key, acconeId.toString())) {
            TAccone accone = new TAccone();
            accone.setAcconeId(acconeId);
            accone.setStatus(1);
            List<TAccone> acconeList = tAcconeMapperDao.selectByCondition(accone);
            if (acconeList != null && acconeList.size() > 0) {
                redisUtil.hset(key, acconeList.get(0).getAcconeId().toString(), acconeList.get(0), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
            } else {
                return null;
            }
        }
        return (TAccone) redisUtil.hget(key, acconeId.toString(), Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

    public void saveAll(List<TAccone> acconeList) {
        List<TAccone> notExists = new ArrayList<>();
        for (TAccone accone : acconeList) {
            accone.setUpdateDate(new Date());
            if (redisUtil.hHasKey(key, accone.getAcconeId().toString())) {
                tAcconeMapperDao.updateById(accone);
            } else {
                notExists.add(accone);
            }
            redisUtil.hset(key, accone.getAcconeId().toString(), accone,
                    Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        }
        if (notExists.size() > 0) {
            tAcconeMapperDao.insertBatch(notExists);
        }
    }

    public void save(TAccone accone) {
        accone.setUpdateDate(new Date());
        if (redisUtil.hHasKey(key, accone.getAcconeId().toString())) {
            tAcconeMapperDao.updateTemplateById(accone);
        } else {
            tAcconeMapperDao.insert(accone);
        }
        redisUtil.hset(key, accone.getAcconeId().toString(), accone,
                Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

    public void saveAcconeStatus(String acconeId, AcconeStatusBean bean) {
        redisUtil.hset(key_saveAcconeStatus, acconeId, bean, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
    }

    public AcconeStatusBean getAcconeStatus(String acconeId) {
        AcconeStatusBean rst = null;
        Object obj = redisUtil.hget(key_saveAcconeStatus, acconeId, Consts.REDIS_CACHE_MASTER_DATA_TIME_OUT);
        if (obj != null) {
            rst = (AcconeStatusBean) obj;
        }
        return rst;
    }
}
