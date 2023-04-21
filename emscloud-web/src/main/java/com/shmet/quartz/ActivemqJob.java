package com.shmet.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.DateTimeUtils;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.RealMetricsItem;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class ActivemqJob implements Job {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  DeviceRealDataDao deviceRealDataDao;
  @Autowired
  private JmsMessagingTemplate jmsMessagingTemplate;
  @Override
  public void execute(JobExecutionContext context) {
    Date date = new Date();
    String hourString = DateTimeUtils.date2String(date, DateTimeUtils.FORMAT_YYYYMMDDHH);
    int hour = Integer.parseInt(hourString);
    try {
      DeviceRealData deviceRealData_em = deviceRealDataDao.findOneByDeviceHour(201800010010040L, hour);
      if (deviceRealData_em != null && deviceRealData_em.getMetrics() != null) {
        List<RealMetricsItem> itemList_em = deviceRealData_em.getMetrics();
        if (itemList_em.size() > 0) {
          RealMetricsItem realMetricsItem_em = itemList_em.get(itemList_em.size() - 1);
          if (realMetricsItem_em.getDatas().get("EPI") != null) {
            Double epi = (((List<Double>)realMetricsItem_em.getDatas().get("EPI"))).get(0);
            realMetricsItem_em.getDatas().put("EPI", epi);
          }
          if (realMetricsItem_em.getDatas().get("EPE") != null) {
            Double epe = (((List<Double>)realMetricsItem_em.getDatas().get("EPE"))).get(0);
            realMetricsItem_em.getDatas().put("EPE", epe);
          }
          Map<String, Object> map = new HashMap<>();
          map.put("time", realMetricsItem_em.getTimestamp());
          map.put("data", realMetricsItem_em.getDatas());
          map.put("deviceCode", "d1");
          map.put("deviceName", "柏树储能电表");
          ObjectMapper om = new ObjectMapper();
          String text = om.writeValueAsString(map);
          jmsMessagingTemplate.convertAndSend("baishu", text);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
