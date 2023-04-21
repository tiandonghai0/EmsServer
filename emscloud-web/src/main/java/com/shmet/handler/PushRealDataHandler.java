package com.shmet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.shmet.enums.AlarmEnum;
import com.shmet.redis.GkSessionCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.ArithUtil;
import com.shmet.Consts;
import com.shmet.NumberBytesUtils;
import com.shmet.bean.GroupStatisticsItem;
import com.shmet.bean.PageDataItem;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.mongodb.DeviceConfigDao;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.mongo.DeviceConfig;
import com.shmet.assembler.Assembler;
import com.shmet.assembler.RealDataParamConvert;
import com.shmet.helper.SpringContextUtil;
import com.shmet.redis.RealDataWebSocketSessionCache;

@Component
public class PushRealDataHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  RealDataWebSocketSessionCache webSocketSessionCache;

  @Autowired
  GkSessionCache gkSessionCache;

  @Autowired
  DeviceConfigDao deviceConfigDao;

  @Value("${jsonFile}")
  String path;

  /**
   * 广播 实时数据 从缓存中取实时数据
   *
   * @param redisCacheDao  redisCacheDao
   * @param socketIoServer socketIoServer
   */
  public void broadcastRealData(RealDataRedisDao redisCacheDao, SocketIOServer socketIoServer) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      //key: sessionId value:List<RealDataItem> 所有session缓存
      Map<String, Object> sessionMap = (Map<String, Object>) webSocketSessionCache.getAllCache();
      //gkSessionCache 只是针对于国开
      Map<String, Object> gkCache = (Map<String, Object>) gkSessionCache.getAllCache();
      //判断是否需要发送
      boolean needSend = false;

      for (String sessionId : sessionMap.keySet()) {
        boolean isGkSessionId = (gkCache != null && gkCache.containsKey(sessionId));
        if (isGkSessionId) {
          List<RealDataItem> filterList = new ArrayList<>();
          List<RealDataItem> gkRealDataItems = (List<RealDataItem>) sessionMap.get(sessionId);
          if (gkRealDataItems == null || gkRealDataItems.isEmpty()) {
            continue;
          }
          for (RealDataItem item : gkRealDataItems) {
            //根据deviceNo 和 tagCode 去缓存中查询匹配的数据 RealDataRedisDao.saveRealDataCache
            Object cacheItem = redisCacheDao.getRealData(Long.parseLong(item.getDeviceNo()), item.getTagCode());
            if (cacheItem != null) {
              needSend = true;
              multiplyingAndSetting(item, (RealDataItem) cacheItem);
              gkRealDataFilter(filterList, item);
            }
          }
          if (needSend) {
            SocketIOClient client = socketIoServer.getClient(UUID.fromString(sessionId));
            if (client != null && client.isChannelOpen()) {
              Assembler assembler =
                  SpringContextUtil.getBean(client.get(Consts.WEB_ASSEMBLE_CLASS));
              JSONObject pageDataItem = client.get(Consts.WEB_ASSEMBLE_PUSH_REQ_MSG);
              client.sendEvent("realData", mapper.writeValueAsString(assembler.assemble(filterList, pageDataItem)));
            }
          }
        } else {
          List<RealDataItem> filterList = new ArrayList<>();
          List<RealDataItem> realDataItems = (List<RealDataItem>) sessionMap.get(sessionId);
          if (realDataItems == null) {
            continue;
          }
          for (RealDataItem item : realDataItems) {
            Object cacheItem = redisCacheDao.getRealData(Long.parseLong(item.getDeviceNo()), item.getTagCode());
            if (cacheItem != null) {
              needSend = true;
              multiplyingAndSetting(item, (RealDataItem) cacheItem);
            }
            filterList.add(item);
          }
          if (needSend) {
            SocketIOClient client = socketIoServer.getClient(UUID.fromString(sessionId));
            if (client != null && client.isChannelOpen()) {
              Assembler assembler = SpringContextUtil.getBean(client.get(Consts.WEB_ASSEMBLE_CLASS));
              JSONObject pageDataItem = client.get(Consts.WEB_ASSEMBLE_PUSH_REQ_MSG);
              client.sendEvent("realData", mapper.writeValueAsString(assembler.assemble(filterList, pageDataItem)));
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error(Consts.MSG_CODE_E010000 + "预期外的异常", e);
    }
  }

  private void gkRealDataFilter(List<RealDataItem> filterList, RealDataItem item) {
    Object data = item.getData();
    if (data != null) {
      if (data instanceof String) {
        String sData = (String) data;
        double d = Double.parseDouble(sData);
        int alarmCode = (int) d;
        item.setContent(AlarmEnum.matchAlarmCode(alarmCode));
      }
    }
    if (StringUtils.isNotBlank(item.getContent())) {
      filterList.add(item);
    }
  }

  public void broadcastGroupRealData(RealDataRedisDao redisCacheDao, SocketIOServer socketIoServer) {
  }

  /**
   * 发送实时数据到前端
   *
   * @param realDataItemList 待发送的数据列表项
   * @param redisCacheDao    redis缓存dao
   * @param client           socketio 客户端
   */
  public void sendRealData(List<RealDataItem> realDataItemList,
                           RealDataRedisDao redisCacheDao,
                           SocketIOClient client,
                           boolean isGkSessionId) throws IOException {
    List<RealDataItem> filterList = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    boolean needSend = false;
    for (RealDataItem item : realDataItemList) {
      Object cacheItem =
          redisCacheDao.getRealData(Long.parseLong(item.getDeviceNo()), item.getTagCode());
      if (cacheItem != null) {
        needSend = true;
        multiplyingAndSetting(item, (RealDataItem) cacheItem);
        if (isGkSessionId) {
          gkRealDataFilter(filterList, item);
        }
      }
    }
    if (needSend) {
      if (client != null && client.isChannelOpen()) {
        Assembler assembler = SpringContextUtil.getBean(client.get(Consts.WEB_ASSEMBLE_CLASS));
        JSONObject pageDataItem = client.get(Consts.WEB_ASSEMBLE_PUSH_REQ_MSG);
        if (isGkSessionId) {
          client.sendEvent("realData", mapper.writeValueAsString(assembler.assemble(filterList, pageDataItem)));
          return;
        }
        client.sendEvent("realData", mapper.writeValueAsString(assembler.assemble(realDataItemList, pageDataItem)));
      }
    }
  }

  private void multiplyingAndSetting(RealDataItem item, RealDataItem cacheItem) {
    item.setDateTime(cacheItem.getDateTime());
    Object data = cacheItem.getData();
    DeviceConfig deviceConfig = deviceConfigDao.findDeviceConfig(Long.parseLong(item.getDeviceNo()), item.getTagCode());
    if (deviceConfig != null) {
      if (deviceConfig.getConfig().containsKey("MultiplyingFactor")) {
        double multiplyingFactor = (Double) deviceConfig.getConfig().get("MultiplyingFactor");
        if (data instanceof List) {
          List<Double> ls = (List<Double>) data;
          for (int i = 0; i < ls.size(); i++) {
            ls.set(i, ArithUtil.mul(ls.get(i), multiplyingFactor));
          }
        } else if (NumberBytesUtils.isDouble(data.toString())) {
          double dataTemp = Double.parseDouble(data.toString());
          data = Double.valueOf(ArithUtil.mul(dataTemp, multiplyingFactor));
        }
      }
    }
    item.setData(data);
  }


  public void sendRealDataDummy(PageDataItem pageDataItem,
                                RealDataRedisDao redisCacheDao,
                                SocketIOClient client) {
    String data = RealDataParamConvert.readFile(pageDataItem.getProjectNo(), pageDataItem.getPageLabel(),path);
    if (client != null && client.isChannelOpen()) {
      client.sendEvent("realData", data);
    }
  }

  public void sendGroupRealData(List<GroupStatisticsItem> realDataItemList,
                                RealDataRedisDao redisCacheDao, SocketIOClient client) throws JsonProcessingException {
  }

}
