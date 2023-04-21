package com.shmet.handler.foreign;

import com.alibaba.fastjson.JSONObject;
import com.shmet.helper.RequestUtils;
import com.shmet.redis.ForeginCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShangHaiShoreHandler {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  ForeginCache foreginCache;

  String url = "http://180.163.178.38:6103/v1";
  String platformID = "425009764";
  String platformSecret = "1234567890abcdef";
  String dataSecret = "1234567890abcdef";
  String dataSecretIV = "1234567890abcdef";
  String sigSecret = "1234567890abcdef";


  //query_token
  public String queryToken() {
    try {
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put("PlatformID", platformID);//岸电平台代码
      dataMap.put("PlatformSecret", platformSecret);//岸电平台分配的唯一识别密钥
      JSONObject resultObj = RequestUtils.post(url + "/query_token", dataMap, JSONObject.class);
      if (resultObj.getInteger("SuccStat") == 0) {
        //成功
        String accessToken = resultObj.getString("AccessToken");
        Integer tokenAvailableTime = resultObj.getInteger("TokenAvailableTime");
        return accessToken;
      } else {
        //失败
      /*0:无；
      1:无此岸电平台；
      2:密钥错误；
      3～99:自定义*/
        Integer failReason = resultObj.getInteger("FailReason");
        logger.info("上港岸电queryToken失败：" + failReason);
        return null;
      }
    } catch (Exception ex) {
      logger.info("上港岸电queryToken错误：" + ex.getMessage());
      return null;
    }

  }

  public void notificationPlatformInfo() {

  }

}
