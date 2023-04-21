package com.shmet.quartz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.DateTimeUtils;
import com.shmet.bean.Ehang365StartStopBean;
import com.shmet.bean.Lock;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mongodb.ShorePowerLogDataDao;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.dao.mysql.mapper.TDeviceMapperDao;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.entity.mongo.ShorePowerLogData;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.TDevice;
import com.shmet.handler.api.EHang365Handler;
import com.shmet.helper.JsonUtils;
import com.shmet.helper.RequestUtils;
import org.beetl.sql.core.query.LambdaQuery;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.shmet.dao.redis.RealDataRedisDao;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class Ehang365Job implements Job {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  RealDataRedisDao redisCacheDao;

  @Autowired
  ShorePowerLogDataDao shorePowerLogDataDao;

  @Autowired
  DeviceRealDataDao deviceRealDataDao;

  @Autowired
  TDeviceDao tDeviceDao;

  @Autowired
  TDeviceMapperDao tDeviceMapperDao;

  @Autowired
  DeviceMapper deviceMapper;

  @Autowired
  RealDataRedisDao realDataRedisDao;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchanges.direct.ehang365eqptstop}")
  String ehang365EqptStopExchangeName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstop.request}")
  String ehang365EqptStopRequestRoutingKeyName;

  static  boolean isRun=false;

  @Override
  public void execute(JobExecutionContext context) {
    if(isRun){
      return;
    }
    isRun=true;
    //System.out.println("Ehang365Job start");
    //logger.info("Ehang365Job start");
    Calendar today = Calendar.getInstance();
      long projectId = 20200009L;
      try {

        try{
          List<ShorePowerLogData> logDataList= shorePowerLogDataDao.getUseElec();
          if(logDataList!=null&&logDataList.size()>0){
            for (ShorePowerLogData data:logDataList
                 ) {
              Long dataTime=data.getTimestamp();
              Long currentTime=DateTimeUtils.dataTimeToLong(today.getTime());
              if(currentTime-dataTime>30 && data.getOrderId()==null && data.getSsType()==1){
//                Ehang365StartStopBean sendData = new Ehang365StartStopBean();
//                sendData.setDeviceId(data.getDeviceId());
//                sendData.setAcconeId(data.getAcconeId());
//                rabbitTemplate.convertAndSend(ehang365EqptStopExchangeName, ehang365EqptStopRequestRoutingKeyName,
//                        sendData, new CorrelationData());
                data.setSsType(2);
                data.setDiff(0.0);
                shorePowerLogDataDao.updateSsType(data);
              }
            }
          }
        }catch (Exception ex){
          logger.error("亨通job getUseElec", ex);
        }


        List<Device> list = deviceMapper.selectList(new QueryWrapper<Device>().like("sub_project_id","20200009"));
        if (list != null && list.size() > 0) {
          for (Device d : list
          ) {
            RealDataItem realDataItemERR3 =
                (RealDataItem) realDataRedisDao.getRealData(d.getDeviceNo(), "ERR3");//故障跳闸
            if (realDataItemERR3 != null) {
              Date realDate =
                  DateTimeUtils
                      .parseDate(realDataItemERR3.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
              long second = DateTimeUtils.differFromSecond(realDate, today.getTime());

              // 5分钟之内的值均可信
              if (second < 300 && Integer.parseInt(realDataItemERR3.getData().toString())==1) { //下发分闸
                Ehang365StartStopBean sendData = new Ehang365StartStopBean();
                sendData.setSendNo(1);
                sendData.setDeviceId(d.getDeviceId());
                sendData.setAcconeId(d.getAcconeId());
                rabbitTemplate.convertAndSend(ehang365EqptStopExchangeName, ehang365EqptStopRequestRoutingKeyName,
                    sendData, new CorrelationData());
                logger.info("Ehang365Job 检测到异常："+d.getDeviceNo());
              }
            }
          }
        }
      } catch (Exception ex) {
        logger.error("亨通job", ex);
      }
      finally {
        isRun=false;
      }

      //智能岸电设备技术对接文档”中第四个接口，即设备异常，回调接口，由于岸电服务不再使用ehang365域名，
    // 请各厂商将“https://ehang365.cn/adback/renren-fast/updateCharging/updateStatus”，修改为“http://36.156.155.131:8090/andianbackb/renren-fast/updateCharging/updateStatus

      // 按要求，去掉自动结束用电
//      int deviceId = 1;
//      JSONObject jsonRst = new JSONObject();
//      ProjectConfig projectConfig = projectConfigDao.findProjectConfigByProjectIdNoCache(projectId);
//      @SuppressWarnings("unchecked")
//      Map<String, Long> eqptMapping = (Map<String, Long>) projectConfig.getConfig().get("eqptMapping");
//      for (String eq : eqptMapping.keySet()
//      ) {
//        Map<String, Integer> qRcodeDeviceId = (Map<String, Integer>) projectConfig.getConfig().get("qRCodeDeviceId");
//        if (qRcodeDeviceId.containsKey(eq)) {
//          deviceId = qRcodeDeviceId.get(eq);
//        }
//        Long acconeId = eqptMapping.get(eq);
//
//        ShorePowerLogData s = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
//        if (s == null || s.getSsType() == 2) {
//          continue;
//        }
//        //表示没有电流
//        Double TPAE = -1.0;
//        Double CA = -1.0;
//        long sSecond = 0;
//        //如果没找到设备编号,为了兼容设备点位编码规则问题
//        TDevice tDevice = new TDevice();
//        tDevice.setAcconeId(s.getAcconeId());
//        tDevice.setDeviceId(s.getDeviceId());
//        //tDevice.setDeviceModel();
//        tDevice.setSubProjectId(s.getSubProjectId());
//        TDevice device = tDeviceDao.findByCondition(tDevice);
//        if (device != null) {
//          Calendar today = Calendar.getInstance();
//          RealDataItem realDataItemCA =
//              (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "CA");
//          if (realDataItemCA != null) {
//            Date realDate =
//                DateTimeUtils
//                    .parseDate(realDataItemCA.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
//            long second = DateTimeUtils.differFromSecond(realDate, today.getTime());
//
//            // 10分钟之内的值均可信
//            if (second < 600) {
//
//              RealDataItem realDataItemTPAE =
//                  (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "TPAE");//总电能
//              if (realDataItemTPAE != null) {
//                TPAE = ((ArrayList<Double>) realDataItemTPAE.getData()).get(0);
//              }
//              if (TPAE == -1.0) {
//                continue;
//              }
//
//              Date sDate =
//                  DateTimeUtils
//                      .parseDate(s.getTimestamp().toString(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
//              sSecond = DateTimeUtils.differFromSecond(sDate, today.getTime());
//              if (sSecond > 600) {//订单超过10分钟，断开
//                CA = Double.parseDouble(realDataItemCA.getData().toString());
//              }
//
//            }
//          }
//
//          if (CA != -1.0 && CA <= 0.0) {
//            //给汇海推送关闭
//            String url = "https://ehang365.cn/adback/renren-fast/updateCharging/updateStatus";
//            Map<String, Object> map = new HashMap();
//            //map.add("recordId", "");
//            map.put("powerOutletId", s.getPowerOutletId());
//            map.put("recordId", s.getOrderId());
//            map.put("endPower", TPAE);
//            Double d = TPAE - s.getCurrentEnergy();
//            BigDecimal b = new BigDecimal(d);
//            d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//            map.put("consumption", d);
//            try {
//              logger.info("岸电回调参数：" + JsonUtils.objToString(map));
//              JSONObject object = RequestUtils.postJson(url, map, JSONObject.class);
//              logger.info("岸电回调结果：" + JsonUtils.objToString(object));
//              if (object.getString("code").equals("0")) {
//                //修改状态为用电结束
//                s.setSsType(2);
//                s.setDiff(TPAE - s.getCurrentEnergy());
//                s.setStartEnergy(s.getCurrentEnergy());
//                s.setCurrentEnergy(TPAE);
//                s.setTimestamp(DateTimeUtils.dataTimeToLong(new Date()));
//                s.setDateTime(DateTimeUtils.dataTimeLongToString(s.getTimestamp()));
//                shorePowerLogDataDao.updateModel(s);
//              } else {
//                if (sSecond > (60 * 60)) {
//                  s.setSsType(2);
//                  s.setDiff(TPAE - s.getCurrentEnergy());
//                  s.setStartEnergy(s.getCurrentEnergy());
//                  s.setCurrentEnergy(TPAE);
//                  s.setTimestamp(DateTimeUtils.dataTimeToLong(new Date()));
//                  s.setDateTime(DateTimeUtils.dataTimeLongToString(s.getTimestamp()));
//                  shorePowerLogDataDao.updateModel(s);
//                }
//                logger.info("岸电回调结果失败：" + JsonUtils.objToString(object));
//              }
//            } catch (Exception ex) {
//              logger.error("岸电回调结果失败：", ex);
//            }
//          }
//        }
//      }

  }
}
