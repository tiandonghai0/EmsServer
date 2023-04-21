package com.shmet.rabbmit;

import java.io.IOException;

import com.shmet.bean.SendTextCmdBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.ChargeDisChargeBean;
import com.shmet.socketio.AcconeSIOEndpoint;

@Component
public class PcscontrolRabbitMQController {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  AcconeSIOEndpoint acconeSIOEndpoint;

  @RabbitListener(queues = "${rabbitmq.queues.pcscontrol.response}")
  public void processPcscontrol(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();
    ChargeDisChargeBean message;
    try {
      message =
          mapper.readValue(new String(msg), ChargeDisChargeBean.class);
      if (message != null) {
        acconeSIOEndpoint.pushPcscontrol(message);
      }
    } catch (IOException e) {
      logger.error(Consts.MSG_CODE_E010000, e);
    }
  }

  @RabbitListener(queues = "${rabbitmq.queues.sendtextcmd.response}")
  public void processSendtextcmd(byte[] msg) {
    ObjectMapper mapper = new ObjectMapper();

    SendTextCmdBean message;
    try {
      message =
          mapper.readValue(new String(msg), SendTextCmdBean.class);
      //logger.info("rabbitmq.queues.sendtextcmd.response监听到消息："+(message==null?"": JsonUtils.objToString(message)));
      if (message != null) {
        acconeSIOEndpoint.pushSendText(message);
      }
    } catch (IOException e) {
      logger.error(Consts.MSG_CODE_E010000, e);
    }
  }

}
