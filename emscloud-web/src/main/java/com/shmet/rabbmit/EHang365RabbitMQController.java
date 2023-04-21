package com.shmet.rabbmit;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.Ehang365StartStopBean;
import com.shmet.bean.SendTextCmdBean;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.redis.RealDataByAcconeWebSocketSessionCache;
import com.shmet.socketio.AcconeSIOEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EHang365RabbitMQController {


  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  AcconeSIOEndpoint acconeSIOEndpoint;

  @RabbitListener(queues = "${rabbitmq.queues.ehang365eqptstop.response}")
  public void ehang365eqptstopresponse(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();

    Ehang365StartStopBean message;
    try {
      message =
          mapper.readValue(new String(msg), Ehang365StartStopBean.class);
      //logger.info("rabbitmq.queues.sendtextcmd.response监听到消息："+(message==null?"": JsonUtils.objToString(message)));
      if (message != null) {
        acconeSIOEndpoint.pushEHang365(message);
      }
    } catch (IOException e) {
      //logger.error(Consts.MSG_CODE_E010000, e);
    }
  }

  @RabbitListener(queues = "${rabbitmq.queues.ehang365eqptstart.response}")
  public void ehang365eqptstartresponse(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();

    Ehang365StartStopBean message;
    try {
      message =
          mapper.readValue(new String(msg), Ehang365StartStopBean.class);
      //logger.info("rabbitmq.queues.sendtextcmd.response监听到消息："+(message==null?"": JsonUtils.objToString(message)));
      if (message != null) {
        acconeSIOEndpoint.pushEHang365(message);
      }
    } catch (IOException e) {
      //logger.error(Consts.MSG_CODE_E010000, e);
    }
  }
}
