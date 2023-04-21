package com.shmet.socketio;

import java.util.UUID;

import com.shmet.bean.*;
import com.shmet.helper.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.redis.UpdateAcconeWebSocketSessionCache;

@Component
public class AcconeSIOEndpoint {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SocketIOServer socketIOServer;

  @Autowired
  UpdateAcconeWebSocketSessionCache webSocketSessionCache;

  @Autowired
  RealDataRedisDao redisCacheDao;

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
  }

  // 消息接收入口
  @OnEvent(value = "setconnectioncache")
  public void updateAccone(SocketIOClient client, AckRequest ackRequest, String acconeId) {
    ObjectMapper mapper = new ObjectMapper();
    PostResponseBean rst = new PostResponseBean();
    rst.setSuccessed(true);
    String rstMsg = "";
    try {
      rstMsg = mapper.writeValueAsString(rst);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    webSocketSessionCache.setCache(acconeId.trim(), client);
    if (ackRequest.isAckRequested()) {
      // send ack response with data to client
      ackRequest.sendAckData(rstMsg);
    }
  }

  // 发送消息
  public void pushVer(UpdateAcconeBean updateAcconeBean) {
    String sessionId = webSocketSessionCache.getCache(updateAcconeBean.getAcconeId().toString());
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      String message;
      if (updateAcconeBean.getStatus() != 0) {
        message = "error";
      } else {
        message = updateAcconeBean.getVer();
      }
      client.sendEvent("updateaccone", message);
    }
  }

  // 发送消息
  public void pushSystemBootStart(SystemBootBean systemBootBean) {
    String sessionId = webSocketSessionCache.getCache(systemBootBean.getAcconeId().toString());
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      client.sendEvent("systemboot", systemBootBean);
    }
  }

  // 发送消息
  public void pushSystemBootStop(SystemBootBean systemBootBean) {
    String sessionId = webSocketSessionCache.getCache(systemBootBean.getAcconeId().toString());
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      client.sendEvent("systemstop", systemBootBean);
    }
  }

  // 发送消息
  public void pushPcscontrol(ChargeDisChargeBean chargeDisCahrgeBean) {
    String sessionId = webSocketSessionCache.getCache(chargeDisCahrgeBean.getAcconeId().toString());
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      client.sendEvent("pcscontrol", chargeDisCahrgeBean);
    }
  }

  // 发送emu指令响应消息
  public void pushSendText(SendTextCmdBean sendTextCmdBean) {

    String sessionId = webSocketSessionCache.getCache(sendTextCmdBean.getAcconeId().toString());
    //logger.info("sendText发送消息开始sessionId:"+(sessionId==null?"":sessionId));
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      logger.info("sendText发送消息：" + JsonUtils.objToString(sendTextCmdBean));
      client.sendEvent("sendText", sendTextCmdBean);
    }
  }
  //船e行响应消息
  public void pushEHang365(Ehang365StartStopBean ehang365StartStopBean){
    String sessionId = webSocketSessionCache.getCache(ehang365StartStopBean.getAcconeId().toString());
    //logger.info("sendText发送消息开始sessionId:"+(sessionId==null?"":sessionId));
    if (sessionId == null) {
      return;
    }
    SocketIOClient client = socketIOServer.getClient(UUID.fromString(sessionId));
    if (client != null && client.isChannelOpen()) {
      logger.info("sendText发送消息：" + JsonUtils.objToString(ehang365StartStopBean));
      client.sendEvent("sendText", ehang365StartStopBean);
    }
  }
}
