package com.shmet.socketio;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.RealDataItem;
import com.shmet.assembler.RealDataParamConvert;
import com.shmet.bean.GroupStatisticsItem;
import com.shmet.bean.PostResponseBean;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.handler.PushRealDataHandler;
import com.shmet.redis.GkSessionCache;
import com.shmet.redis.RealDataWebSocketSessionCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RealDataSIOEndpoint {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  //----------仅针对国开事件进行特殊处理-------
  public static final String PROJECTNO = "gk";
  public static final String PAGELABEL = "alarm_real";

  //--------------------------------------

  @Autowired
  RealDataWebSocketSessionCache webSocketSessionCache;

  @Autowired
  PushRealDataHandler pushRealDataHandler;

  @Autowired
  RealDataRedisDao redisCacheDao;

  @Autowired
  GkSessionCache gkSessionCache;

  @Value("${jsonFile}")
  String path;

  // 添加connect事件，当客户端发起连接时调用
  @OnConnect
  public void onConnect(SocketIOClient client) {
  }

  // 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
  @OnDisconnect
  public void onDisconnect(SocketIOClient client) {
    logger.info("客户端断开连接, sessionId=" + client.getSessionId().toString());
    client.disconnect();
    webSocketSessionCache.delCache(client);
    gkSessionCache.delCache(client);
    webSocketSessionCache.delGroupCache(client);
  }

  //消息接收入口  事件名 监听 SocketIOClient 发送的数据
  @OnEvent(value = "realData")
  public void pushPageData(SocketIOClient client, AckRequest ackRequest, String message) {
    ObjectMapper mapper = new ObjectMapper();
    JSONObject pageDataItem = (JSONObject) JSONObject.parse(message);

    PostResponseBean rst = new PostResponseBean();
    //判断是否是国开缓存
    boolean isGkSessionId = false;
    if (pageDataItem != null) {
      System.out.println("filepath:"+path);
      //将传入的对象拼装成realDataItemList
      List<RealDataItem> realDataItemList = RealDataParamConvert.convert(pageDataItem,path);
      client.set(Consts.WEB_ASSEMBLE_CLASS, "realDataAssembler");
      client.set(Consts.WEB_ASSEMBLE_PUSH_REQ_MSG, pageDataItem);
      //判定是不是国开报警项  如果是国开报警项 就放入 GkSessionCache.websocketsession
      // 否则放入缓存 RealDataWebSocketSessionCache.websocketsession
      if (StringUtils.equals(PROJECTNO, pageDataItem.getString("projectNo"))
          && StringUtils.equals(PAGELABEL, pageDataItem.getString("pageLabel"))) {
        realDataItemList.removeIf(o -> StringUtils.isBlank(o.getUnit()));
        gkSessionCache.setCache(client, realDataItemList);
        isGkSessionId = true;
      }
      webSocketSessionCache.setCache(client, realDataItemList);
      rst.setSuccessed(true);
      String rstMsg = "";
      try {
        rstMsg = mapper.writeValueAsString(rst);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      if (ackRequest.isAckRequested()) {
        // send ack response with data to client
        ackRequest.sendAckData(rstMsg);
      }
      try {
        //执行发送逻辑
        pushRealDataHandler.sendRealData(realDataItemList, redisCacheDao, client, isGkSessionId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      rst.setSuccessed(false);
      rst.setErrorCode(Consts.MSG_CODE_E000005);
    }
  }

  // 消息接收入口
  @OnEvent(value = "grouprealdata")
  public void groupRealData(SocketIOClient client, AckRequest ackRequest, String message) {
    ObjectMapper mapper = new ObjectMapper();
    List<GroupStatisticsItem> realDataItemList = null;
    try {
      realDataItemList = mapper.readValue(message, new TypeReference<List<GroupStatisticsItem>>() {
      });
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    PostResponseBean rst = new PostResponseBean();
    if (realDataItemList != null && realDataItemList.size() > 0) {
      webSocketSessionCache.setGroupCache(client, realDataItemList);
      rst.setSuccessed(true);
      String rstMsg = "";
      try {
        rstMsg = mapper.writeValueAsString(rst);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      if (ackRequest.isAckRequested()) {
        // send ack response with data to client
        ackRequest.sendAckData(rstMsg);
      }
      try {
        pushRealDataHandler.sendGroupRealData(realDataItemList, redisCacheDao, client);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    } else {
      rst.setSuccessed(false);
      rst.setErrorCode(Consts.MSG_CODE_E000005);
    }
  }
}
