package com.shmet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitConfig {

  @Value("${rabbitmq.queues.devicerealdata.disp}")
  String realDataQueueDispName;

  @Value("${rabbitmq.queues.updateaccone.request}")
  String updateAcconeReqName;

  @Value("${rabbitmq.queues.updateaccone.response}")
  String updateAcconeResName;

  @Value("${rabbitmq.queues.systemboot.request}")
  String systemBootReqName;

  @Value("${rabbitmq.queues.systemboot.response}")
  String systemBootResName;

  @Value("${rabbitmq.queues.systemstop.request}")
  String systemStopReqName;

  @Value("${rabbitmq.queues.systemstop.response}")
  String systemStopResName;

  @Value("${rabbitmq.queues.ehang365eqptstart.request}")
  String ehang365EqptStartReqName;

  @Value("${rabbitmq.queues.ehang365eqptstart.response}")
  String ehang365EqptStartResName;

  @Value("${rabbitmq.queues.ehang365eqptstop.request}")
  String ehang365EqptStopReqName;

  @Value("${rabbitmq.queues.ehang365eqptstop.response}")
  String ehang365EqptStopResName;

  @Value("${rabbitmq.queues.pcscontrol.request}")
  String pcscontrolReqName;

  @Value("${rabbitmq.queues.pcscontrol.response}")
  String pcscontrolResName;

  @Value("${rabbitmq.queues.getbatdata.request}")
  String getbatdataName;


  @Value("${rabbitmq.exchanges.topic.realdata}")
  String realDataExchangeName;

  @Value("${rabbitmq.routingkeys.devicerealdata}")
  String deviceRealDataRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.updateaccone}")
  String updateAcconeExchangeName;

  @Value("${rabbitmq.routingkeys.updateaccone.request}")
  String updateAcconeRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.updateaccone.response}")
  String updateAcconeResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.systemboot}")
  String systemBootExchangeName;

  @Value("${rabbitmq.routingkeys.systemboot.request}")
  String systemBootRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.systemboot.response}")
  String systemBootResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.systemstop}")
  String systemStopExchangeName;

  @Value("${rabbitmq.routingkeys.systemstop.request}")
  String systemStopRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.systemstop.response}")
  String systemStopResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.ehang365eqptstart}")
  String ehang365EqptStartExchangeName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstart.request}")
  String ehang365EqptStartRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstart.response}")
  String ehang365EqptStartResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.ehang365eqptstop}")
  String ehang365EqptStopExchangeName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstop.request}")
  String ehang365EqptStopRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstop.response}")
  String ehang365EqptStopResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.pcscontrol}")
  String pcscontrolExchangeName;

  @Value("${rabbitmq.exchanges.direct.getbatdata}")
  String getbatdataExchangeName;

  @Value("${rabbitmq.routingkeys.pcscontrol.request}")
  String pcscontrolRequestRoutingKeyName;

  @Value("${rabbitmq.routingkeys.pcscontrol.response}")
  String pcscontrolResponseRoutingKeyName;

  @Value("${rabbitmq.routingkeys.getbatdata.request}")
  String getbatdataRoutingKeyName;


  @Value("${rabbitmq.queues.watermeter.canceltask}")
  String canceltaskName;

  @Value("${rabbitmq.queues.watermeter.rechargetask}")
  String rechargetaskName;

  @Value("${rabbitmq.routingkeys.watermeter.canceltask}")
  String canceltaskRoutingKey;

  @Value("${rabbitmq.routingkeys.watermeter.rechargetask}")
  String rechargetaskRoutingKey;

  @Value("${rabbitmq.exchanges.watermeter.canceltask}")
  String canceltaskExchangeName;

  @Value("${rabbitmq.exchanges.watermeter.rechargetask}")
  String rechargetaskExchangeName;

  @Value("${rabbitmq.exchanges.watermeter.recordtask}")
  String recordtaskExchangeName;

  @Value("${rabbitmq.routingkeys.watermeter.recordtask}")
  String recordtaskRoutingKey;

  @Value("${rabbitmq.queues.watermeter.recordtask}")
  String recordtaskName;

  /**
   * 调试命令
   */
  @Value("${rabbitmq.queues.sendtextcmd.request}")
  String sendtextcmdName;

  @Value("${rabbitmq.exchanges.direct.sendtextcmd}")
  String sendtextcmdExchangeName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.request}")
  String sendtextcmdRoutingKeyName;

  @Value("${rabbitmq.queues.sendtextcmd.response}")
  String sendtextcmdResQueName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.response}")
  String sendtextcmdResponseRoutingKeyName;


  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Resource
  private RabbitTemplate rabbitTemplate;

  /**
   * 定制化amqp模版 可根据需要定制多个
   * <p>
   * <p>
   * 此处为模版类定义 Jackson消息转换器 ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调 即消息发送到exchange ack
   * ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调 即消息发送不到任何一个队列中 ack
   *
   * @return the amqp template
   */
  // @Primary
  @Bean
  public AmqpTemplate amqpTemplate() {
    // 使用jackson 消息转换器
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    rabbitTemplate.setEncoding("UTF-8");
    // 开启returncallback yml 需要 配置 publisher-returns: true
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
      String correlationId = message.getMessageProperties().getCorrelationId();
      logger.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText,
          exchange, routingKey);
    });
    // 消息确认 yml 需要配置 publisher-returns: true
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
      if (ack) {
        logger.debug("消息发送到exchange成功,id: {}", correlationData.getId());
      } else {
        logger.debug("消息发送到exchange失败,原因: {}", cause);
      }
    });
    return rabbitTemplate;
  }

  /**
   * 接收实时数据并做页面展示的队列 应与Topic类型交换机绑定
   */
  @Bean
  public Queue realDataDispQueue() {
    return new Queue(realDataQueueDispName, true);
  }

  /**
   * accone 固件更新 请求
   */
  @Bean
  public Queue acconeUpdateReqQueue() {
    return new Queue(updateAcconeReqName, false, false, true);
  }

  /**
   * accone 固件更新 响应
   */
  @Bean
  public Queue acconeUpdateResQueue() {
    return new Queue(updateAcconeResName, false, false, true);
  }

  /**
   * accone系统 启动 请求
   */
  @Bean
  public Queue systemBootReqQueue() {
    return new Queue(systemBootReqName, false, false, true);
  }

  /**
   * accone系统 启动 响应
   */
  @Bean
  public Queue systemBootResQueue() {
    return new Queue(systemBootResName, false, false, true);
  }

  /**
   * accone系统 停止 请求
   */
  @Bean
  public Queue systemStopReqQueue() {
    return new Queue(systemStopReqName, false, false, true);
  }

  /**
   * accone系统 停止 响应
   */
  @Bean
  public Queue systemStopResQueue() {
    return new Queue(systemStopResName, false, false, true);
  }

  /**
   * ehang 亨通岸电设备 开始用电 请求
   */
  @Bean
  public Queue ehang365EqptStartReqQueue() {
    return new Queue(ehang365EqptStartReqName, false, false, true);
  }

  /**
   * ehang 亨通岸电设备 开始用电 响应
   */
  @Bean
  public Queue ehang365EqptStartResQueue() {
    return new Queue(ehang365EqptStartResName, false, false, true);
  }

  /**
   * ehang 亨通岸电设备 停止用电 请求
   */
  @Bean
  public Queue ehang365EqptStopReqQueue() {
    return new Queue(ehang365EqptStopReqName, false, false, true);
  }

  /**
   * ehang 亨通岸电设备 停止用电 响应
   */
  @Bean
  public Queue ehang365EqptStopResQueue() {
    return new Queue(ehang365EqptStopResName, false, false, true);
  }


  /**
   * 开设备请求
   */
  @Bean
  public Queue pcscontrolReqQueue() {
    return new Queue(pcscontrolReqName, false, false, true);
  }

  /**
   * 开设备 响应
   */
  @Bean
  public Queue pcscontrolResQueue() {
    return new Queue(pcscontrolResName, false, false, true);
  }

  @Bean
  public Queue getbatdataQueue() {
    return new Queue(getbatdataName, false, false, true);
  }

  @Bean
  public Queue sendtextcmdQueue() {
    return new Queue(sendtextcmdName, false, false, true);
  }

  @Bean
  TopicExchange realDataExchange() {
    return new TopicExchange(realDataExchangeName);
  }

  @Bean
  DirectExchange updateAcconeExchange() {
    return new DirectExchange(updateAcconeExchangeName, false, true);
  }

  @Bean
  DirectExchange systemBootExchange() {
    return new DirectExchange(systemBootExchangeName, false, true);
  }

  @Bean
  DirectExchange systemStopExchange() {
    return new DirectExchange(systemStopExchangeName, false, true);
  }

  @Bean
  DirectExchange ehang365EqptStartExchange() {
    return new DirectExchange(ehang365EqptStartExchangeName, false, true);
  }

  @Bean
  DirectExchange ehang365EqptStopExchange() {
    return new DirectExchange(ehang365EqptStopExchangeName, false, true);
  }


  @Bean
  DirectExchange pcscontrolExchange() {
    return new DirectExchange(pcscontrolExchangeName, false, true);
  }

  @Bean
  DirectExchange getbatdataExchange() {
    return new DirectExchange(getbatdataExchangeName, false, true);
  }

  @Bean
  DirectExchange sendtextcmdExchange() {
    return new DirectExchange(sendtextcmdExchangeName, false, true);
  }

  @Bean
  public Binding bindingRealDataDisp() {
    return BindingBuilder.bind(realDataDispQueue()).to(realDataExchange())
        .with(deviceRealDataRoutingKeyName);
  }

  @Bean
  public Binding bindingUpdateAcconeRequest() {
    return BindingBuilder.bind(acconeUpdateReqQueue()).to(updateAcconeExchange())
        .with(updateAcconeRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingUpdateAcconeResponse() {
    return BindingBuilder.bind(acconeUpdateResQueue()).to(updateAcconeExchange())
        .with(updateAcconeResponseRoutingKeyName);
  }

  @Bean
  public Binding bindingSystemBootRequest() {
    return BindingBuilder.bind(systemBootReqQueue()).to(systemBootExchange())
        .with(systemBootRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingSystemBootResponse() {
    return BindingBuilder.bind(systemBootResQueue()).to(systemBootExchange())
        .with(systemBootResponseRoutingKeyName);
  }

  @Bean
  public Binding bindingSystemStopRequest() {
    return BindingBuilder.bind(systemStopReqQueue()).to(systemStopExchange())
        .with(systemStopRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingSystemStopResponse() {
    return BindingBuilder.bind(systemStopResQueue()).to(systemStopExchange())
        .with(systemStopResponseRoutingKeyName);
  }

  @Bean
  public Binding bindingEhang365EqptStartRequest() {
    return BindingBuilder.bind(ehang365EqptStartReqQueue()).to(ehang365EqptStartExchange())
        .with(ehang365EqptStartRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingEhang365EqptStartResponse() {
    return BindingBuilder.bind(ehang365EqptStartResQueue()).to(ehang365EqptStartExchange())
        .with(ehang365EqptStartResponseRoutingKeyName);
  }

  @Bean
  public Binding bindingEhang365EqptStopRequest() {
    return BindingBuilder.bind(ehang365EqptStopReqQueue()).to(ehang365EqptStopExchange())
        .with(ehang365EqptStopRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingEhang365EqptStopResponse() {
    return BindingBuilder.bind(ehang365EqptStopResQueue()).to(ehang365EqptStopExchange())
        .with(ehang365EqptStopResponseRoutingKeyName);
  }


  @Bean
  public Binding bindingPcscontrolRequest() {
    return BindingBuilder.bind(pcscontrolReqQueue()).to(pcscontrolExchange())
        .with(pcscontrolRequestRoutingKeyName);
  }

  @Bean
  public Binding bindingPcscontrolResponse() {
    return BindingBuilder.bind(pcscontrolResQueue()).to(pcscontrolExchange())
        .with(pcscontrolResponseRoutingKeyName);
  }

  @Bean
  public Binding bindingGetbatdataResponse() {
    return BindingBuilder.bind(getbatdataQueue()).to(getbatdataExchange())
        .with(getbatdataRoutingKeyName);
  }

  @Bean
  public Binding bindingSendtextcmdResponse() {
    return BindingBuilder.bind(sendtextcmdQueue()).to(sendtextcmdExchange())
        .with(sendtextcmdRoutingKeyName);
  }

  /**************对接大华水表*************/
  @Bean
  public Queue canceltaskQueue() {
    return new Queue(canceltaskName, false, false, true);
  }

  @Bean
  public Queue rechargetaskQueue() {
    return new Queue(rechargetaskName, false, false, true);
  }

  @Bean
  public Queue recordtaskQueue() {
    return new Queue(recordtaskName, false, false, true);
  }

  @Bean
  DirectExchange canceltaskExchange() {
    return new DirectExchange(canceltaskExchangeName, false, true);
  }

  @Bean
  DirectExchange rechargetaskExchange() {
    return new DirectExchange(rechargetaskExchangeName, false, true);
  }

  @Bean
  DirectExchange recordtaskExchange() {
    return new DirectExchange(recordtaskExchangeName, false, true);
  }


  @Bean
  public Binding bindingCanceltask() {
    return BindingBuilder.bind(canceltaskQueue()).to(canceltaskExchange())
        .with(canceltaskRoutingKey);
  }

  @Bean
  public Binding bindingRechargetask() {
    return BindingBuilder.bind(rechargetaskQueue()).to(rechargetaskExchange())
        .with(rechargetaskRoutingKey);
  }

  @Bean
  public Binding bindingRecordtask() {
    return BindingBuilder.bind(recordtaskQueue()).to(recordtaskExchange())
        .with(recordtaskRoutingKey);
  }

  /**
   * 调试命令
   */
  @Bean
  DirectExchange sendtextcmdResExchange() {
    return new DirectExchange(sendtextcmdExchangeName, false, true);
  }

  @Bean
  public Queue sendtextcmdResQueue() {
    return new Queue(sendtextcmdResQueName, false, false, true);
  }

  @Bean
  public Binding bindingSendtextcmdResResponse() {
    return BindingBuilder.bind(sendtextcmdResQueue()).to(sendtextcmdResExchange())
        .with(sendtextcmdResponseRoutingKeyName);
  }

}
