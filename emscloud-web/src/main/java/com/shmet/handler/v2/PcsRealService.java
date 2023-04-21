package com.shmet.handler.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shmet.bean.RealDataItem;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PcsRealService {

  //private static final Logger log = LoggerFactory.getLogger(PcsRealService.class);
  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";

  @Autowired
  private LoginService loginService;

  @Resource
  private MongoTemplate mongoTemplate;

  @Autowired
  private CommonService commonService;

  public Map<Long, JSONObject> getPcsRealDataFromRedis(Long subprojectId) {
    Map<Long, JSONObject> map = new LinkedHashMap<>();
    Query query = new Query(Criteria.where("subProjectId").is(subprojectId));
    ProjectConfig projectConfig = mongoTemplate.findOne(query, ProjectConfig.class, "projectConfig");
    List<Long> pscDeviceNos = loginService.getPcsDeviceNosBySubProjectId(subprojectId);

    pscDeviceNos.forEach(dno -> {
      JSONObject jsonObj = new JSONObject();
      Set<String> items = commonService.listKeysByPattern(REALDATA_CACHE, dno + ".*");
      items.forEach(item -> {
        RealDataItem realDataItem = JSON.parseObject(item, RealDataItem.class);
        if (StringUtils.equalsIgnoreCase("AP", realDataItem.getTagCode())) {
          if (projectConfig == null) {
            jsonObj.put("AP", realDataItem.getData());
          } else {
            jsonObj.put("AP", Double.parseDouble((String) realDataItem.getData()) * projectConfig.getPowerFactor());
          }
        }
        jsonObj.put(realDataItem.getTagCode(), realDataItem.getData());
        jsonObj.put("calldate", DateUtils.strConvertDateStr(realDataItem.getDateTime(), "-", ":"));
      });
      map.put(dno, jsonObj);
    });
    return map;
  }
}
