package com.shmet.socketio;

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
import com.shmet.Consts;
import com.shmet.bean.PostResponseBean;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.handler.PushRealDataHandler;
import com.shmet.redis.RealDataByAcconeWebSocketSessionCache;

@Component
public class RealDataByAcconeSIOEndpoint {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SocketIOServer socketIOServer;

  @Autowired
  RealDataByAcconeWebSocketSessionCache webSocketSessionCache;

  @Autowired
  PushRealDataHandler pushRealDataHandler;

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

  //消息接收入口
  @OnEvent(value = "realDataByAccone")
  public void pushPageData(SocketIOClient client, AckRequest ackRequest, String message) {
    ObjectMapper mapper = new ObjectMapper();

    PostResponseBean rst = new PostResponseBean();
    if (message != null) {
      webSocketSessionCache.setCache(client, message.trim());
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
    } else {
      rst.setSuccessed(false);
      rst.setErrorCode(Consts.MSG_CODE_E000005);
    }
  }
}
