package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shmet.dao.AppVersionMapper;
import com.shmet.entity.mysql.gen.AppVersion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author
 */
@Service
public class AppVersionService {

  @Resource
  private AppVersionMapper appVersionMapper;

  public AppVersion queryLatestApp(String v) {
    return appVersionMapper.queryLatestApp(v);
  }

  public AppVersion getAppVersion(String v,String device){
    LambdaQueryWrapper<AppVersion> query=new LambdaQueryWrapper<>();
    query.eq(AppVersion::getDevice,device);
    query.eq(AppVersion::getV,v);
    query.orderByDesc(AppVersion::getCreatetime);
    query.last("LIMIT 1");
   return appVersionMapper.selectOne(query);
  }

  public Integer addAppVersion(AppVersion appVersion){
    return appVersionMapper.insert(appVersion);
  }
}
