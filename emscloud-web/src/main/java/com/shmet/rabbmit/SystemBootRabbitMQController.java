package com.shmet.rabbmit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.bean.SystemBootBean;
import com.shmet.socketio.AcconeSIOEndpoint;

@Component
public class SystemBootRabbitMQController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AcconeSIOEndpoint acconeSIOEndpoint;

    @RabbitListener(queues = "${rabbitmq.queues.systemboot.response}")
    public void processSystemBoot(byte[] msg) {
        ObjectMapper mapper = new ObjectMapper();
        SystemBootBean message;
        try {
            message =
                    mapper.readValue(new String(msg), SystemBootBean.class);
            if (message != null) {
                acconeSIOEndpoint.pushSystemBootStart(message);
            }
        } catch (IOException e) {
            logger.error(Consts.MSG_CODE_E010000, e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.systemstop.response}")
    public void processSystemStop(byte[] msg) {
        ObjectMapper mapper = new ObjectMapper();
        SystemBootBean message;
        try {
            message =
                    mapper.readValue(new String(msg), SystemBootBean.class);
            if (message != null) {
                acconeSIOEndpoint.pushSystemBootStop(message);
            }
        } catch (IOException e) {
            logger.error(Consts.MSG_CODE_E010000, e);
        }
    }
}
