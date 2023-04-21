package com.shmet.rabbmit;

import java.io.IOException;
import java.util.*;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.bean.AcconeDebugLogBean;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.redis.RealDataByAcconeWebSocketSessionCache;

@Component
public class MessageReceiveRabbitMQController {

  @Autowired
  RealDataByAcconeWebSocketSessionCache webSocketSessionCache;

  @Autowired
  RealDataRedisDao redisCacheDao;

  @Autowired
  SocketIOServer socketIoServer;

  /**
   * 监听  接收实时数据队列
   *
   * @param msg 数据
   */
  @RabbitListener(queues = "${rabbitmq.queues.devicerealdata.disp}")
  public void processRealDataDisp(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();
    AcconeDebugLogBean acconeDebugLogBean = new AcconeDebugLogBean();
    try {
        String content = new String(msg);
        acconeDebugLogBean = mapper.readValue(content, acconeDebugLogBean.getClass());
        Collection<SocketIOClient> socketIOClientCollection = socketIoServer.getAllClients();
        if (socketIOClientCollection == null || socketIOClientCollection.size() == 0) {
          return;
        }
        Map<String, Object> clientMap = (LinkedHashMap<String, Object>) webSocketSessionCache.getAllCache();
        if (clientMap == null || clientMap.size() == 0) {
          return;
        }
        for (SocketIOClient socketIOClient : socketIOClientCollection) {
          if (clientMap.containsKey(socketIOClient.getSessionId().toString()) && acconeDebugLogBean.getAcconeId() == Long.parseLong(clientMap.get(socketIOClient.getSessionId().toString()).toString())) {
            if (socketIOClient.isChannelOpen()) {
              System.out.println("向客户端发送Accone数据:AcconeId:=" + acconeDebugLogBean.getAcconeId());
              socketIOClient.sendEvent("realDataByAccone", mapper.writeValueAsString(acconeDebugLogBean));
            }

          }
        }
      //}
      // 根据消息中的deviceid，筛选应该刷新的session
      // 从缓存中获取session，根据deviceid 以及 session中注册的点位信息，获取相应的数据，组合数据并发送给客户端。
//      if(acconeDebugLogBean.getAcconeId()==867605051287579L){
//      Map<String, Object> clientMap = (LinkedHashMap<String, Object>) webSocketSessionCache.getAllCache();
//      if (clientMap != null && !clientMap.isEmpty()) {
//        for (String sessionId : clientMap.keySet()) {
//          //List<Long> acconeIdList = (List<Long>) clientMap.get(sessionId);
//          Long acconeId = Long.parseLong(clientMap.get(sessionId).toString());
//          //for (Long acconeId : acconeIdList) {
//            if (acconeId == acconeDebugLogBean.getAcconeId()) {
//
//              SocketIOClient client = socketIoServer.getClient(UUID.fromString(sessionId));
//              if (client != null && client.isChannelOpen()) {
//                System.out.println("向客户端发送Accone数据:AcconeId:=" + acconeDebugLogBean.getAcconeId());
//                client.sendEvent("realDataByAccone", mapper.writeValueAsString(acconeDebugLogBean));
//              }
//            }
//          //}
//        }
//      }
//      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
