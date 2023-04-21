package com.shmet.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.shmet.Consts;
import com.shmet.bean.GetBatDataBean;
import com.shmet.bean.PostResponseBean;
import com.shmet.bean.StatisticsRequest;
import com.shmet.dao.mysql.TAcconeDao;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.handler.StatisticsHandler;

@Controller
public class StatisticsController {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  StatisticsHandler stisticsHandler;

  @Value("${rabbitmq.exchanges.direct.getbatdata}")
  String getbatdataExchangeName;

  @Value("${rabbitmq.routingkeys.getbatdata.request}")
  String getbatdataRequestRoutingKeyName;

  @Autowired
  TAcconeDao acconeDao;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * 内部使用
   *
   * @return ModelAndView
   */
  @RequestMapping("/statistics/statistics/init")
  public ModelAndView init() {
    return new ModelAndView("/statistics/statistics.btl");
  }

  /**
   * 按小时查询数据
   *
   * @param statisticsBean statisticsBean
   * @return jsonString
   */
  @ResponseBody
  @RequestMapping(value = "/statistics/single/hour", method = RequestMethod.POST)
  public String hourStatistics(@RequestBody StatisticsRequest statisticsBean) {
    String rst = null;
    try {
      rst = stisticsHandler.batchSingleStatistics(statisticsBean, Consts.TIME_TYPE_HOUR);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(Consts.MSG_CODE_E000001, e);
    }

    return rst;
  }

  /**
   * 按天查询数据
   *
   * @param statisticsBean statisticsBean
   * @return jsonString
   */
  @ResponseBody
  @RequestMapping(value = "/statistics/single/day", method = RequestMethod.POST)
  public String dayStatistics(@RequestBody StatisticsRequest statisticsBean) {
    String rst = null;
    try {
      rst = stisticsHandler.batchSingleStatistics(statisticsBean, Consts.TIME_TYPE_DAY);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(Consts.MSG_CODE_E000001, e);
    }

    return rst;
  }

  /**
   * 按月查询数据
   *
   * @param statisticsBean statisticsBean
   * @return jsonString
   */
  @ResponseBody
  @RequestMapping(value = "/statistics/single/month", method = RequestMethod.POST)
  public String monthStatistics(@RequestBody StatisticsRequest statisticsBean) {
    String rst = null;
    try {
      rst = stisticsHandler.batchSingleStatistics(statisticsBean, Consts.TIME_TYPE_MONTH);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(Consts.MSG_CODE_E000001, e);
    }

    return rst;
  }

  /**
   * 实时数据
   *
   * @param statisticsBean statisticsBean
   * @return jsonString
   */
  @ResponseBody
  @RequestMapping(value = "/statistics/single/real", method = RequestMethod.POST)
  public String realData(@RequestBody StatisticsRequest statisticsBean) {
    String rst = null;
    try {
      long start = System.currentTimeMillis();
      rst = stisticsHandler.batchSingleStatistics(statisticsBean, Consts.TIME_TYPE_REAL);
      logger.error("耗时: " + (System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(Consts.MSG_CODE_E000001, e);
    }
    return rst;
  }

  /**
   * 统计告警数据
   *
   * @param statisticsBean statisticsBean
   * @return jsonString
   */
  @ResponseBody
  @PostMapping(value = "/statistics/fault/real")
  public String getFaultRealData(@RequestBody StatisticsRequest statisticsBean) {
    String rst = null;
    try {
      rst = stisticsHandler.batchFaultStatistics(statisticsBean, Consts.TIME_TYPE_REAL);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(Consts.MSG_CODE_E000001, e);
    }
    return rst;
  }

  /**
   * accone 数据
   *
   * @param getBatDataBean getBatDataBean
   * @return PostResponseBean
   */
  @ResponseBody
  @PostMapping(value = "/accone/getbatdata")
  public PostResponseBean getbatdataProcess(@RequestBody GetBatDataBean getBatDataBean) {
    PostResponseBean rst = new PostResponseBean();
    Long acconeId = getBatDataBean.getAcconeId();
    TAccone accone = acconeDao.findAcconeByAcconeId(acconeId);
    if (accone == null) {
      rst.setErrorMsg("Accone不存在，AcconeID:=" + acconeId);
      rst.setSuccessed(false);
      return rst;
    }
    try {
      List<GetBatDataBean> getBatDataBeanList = new ArrayList<>();
      getBatDataBeanList.add(getBatDataBean);
      rabbitTemplate.convertAndSend(getbatdataExchangeName, getbatdataRequestRoutingKeyName,
          getBatDataBeanList, new CorrelationData());
      rst.setSuccessed(true);
    } catch (Throwable t) {
      rst.setSuccessed(false);
      rst.setErrorMsg(t.getMessage());
      t.printStackTrace();
    }
    return rst;
  }

  @ResponseBody
  @GetMapping(value = "/get/data")
  public Object getbatdataProcess(String date) {
    return stisticsHandler.getDeviceListByDate(date);
  }

}
