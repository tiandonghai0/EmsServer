package com.shmet.rabbmit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.UpdateAcconeBean;
import com.shmet.socketio.AcconeSIOEndpoint;

@Component
public class UpdateAcconeRabbitMQController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AcconeSIOEndpoint updateAcconeSIOEndpoint;

    @RabbitListener(queues = "${rabbitmq.queues.updateaccone.response}")
    public void processRealDataDisp(byte[] msg) {
        ObjectMapper mapper = new ObjectMapper();
        UpdateAcconeBean message;
        try {
            message =
                    mapper.readValue(new String(msg), UpdateAcconeBean.class);
            if (message != null) {
                updateAcconeSIOEndpoint.pushVer(message);
            }
        } catch (IOException e) {
            logger.error(Consts.MSG_CODE_E010000, e);
        }
    }
}
