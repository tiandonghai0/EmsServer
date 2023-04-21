package com.shmet.handler.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.shmet.ArithUtil;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.mysql.gen.TDevice;
import com.shmet.helper.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.shmet.Consts;
import com.shmet.DateTimeUtils;
import com.shmet.bean.EHang365StartStopReqBean;
import com.shmet.bean.Ehang365StartStopBean;
import com.shmet.bean.Lock;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mongodb.ShorePowerLogDataDao;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.dao.redis.ShorePowerRedisDao;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.ShorePowerLogData;
import com.shmet.exception.EmsBisException;
import com.shmet.helper.mongo.MongoBathUpdateOptions;

@Component
public class EHang365Handler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${rabbitmq.exchanges.direct.ehang365eqptstart}")
    String ehang365EqptStartExchangeName;

    @Value("${rabbitmq.routingkeys.ehang365eqptstart.request}")
    String ehang365EqptStartRequestRoutingKeyName;

    @Value("${rabbitmq.exchanges.direct.ehang365eqptstop}")
    String ehang365EqptStopExchangeName;

    @Value("${rabbitmq.routingkeys.ehang365eqptstop.request}")
    String ehang365EqptStopRequestRoutingKeyName;

    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    TDeviceDao deviceDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EHang365Listener eHang365Listener;

    @Autowired
    ShorePowerRedisDao shorePowerRedisDao;

    @Autowired
    ShorePowerLogDataDao shorePowerLogDataDao;

    @Autowired
    RealDataRedisDao realDataRedisDao;

    // 20秒超时
    int timeout = 20 * 1000;

    public JSONObject getRefreshPower(EHang365StartStopReqBean param) throws Throwable {
        logger.info("getRefreshPower-param:" + JsonUtils.objToString(param));
        TDevice device = deviceDao.findByAnDianId(param.getPowerOutletId());
        if (device == null) {
            //江苏亨通
            return getRefreshPower(param, 1);
        } else {
            //国信1#、2#以及其他

            return getRefreshPower2(param, device);
        }

    }
    public JSONObject getRefreshPower2(EHang365StartStopReqBean param, TDevice device) throws Throwable {
        JSONObject jsonRst = new JSONObject();
        try {
            //判断是否正在用电
            ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(device.getAcconeId(), device.getDeviceId());
            if (logData == null) {
                throw new EmsBisException("订单不存在");
            }
            if (logData.getSsType() == 2) {
                jsonRst.put("time", logData.getDateTime());
                jsonRst.put("power", logData.getCurrentEnergy());
                jsonRst.put("consumption", 0);
                jsonRst.put("status", 1);
                return jsonRst;
            }

            boolean isOnline = false;
            double energy = 0.0d;
            RealDataItem realDataItem =
                    (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "D_TE");
            if (realDataItem != null) {
                Date realDate =
                        DateTimeUtils
                                .parseDate(realDataItem.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
                long second = DateTimeUtils.differFromSecond(realDate, Calendar.getInstance().getTime());

                // 5分钟之内的值均可信
                if (second < 300) {
                    isOnline = true;
                    energy = Double.parseDouble(realDataItem.getData().toString());
                }
            }
            if (isOnline == false) {
                throw new EmsBisException("设备不在线");
            }
            //ShorePowerLogData logData = new ShorePowerLogData();

            Double d = ArithUtil.sub(energy, logData.getStartEnergy());
            BigDecimal b = new BigDecimal(d);
            d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            jsonRst.put("time", logData.getDateTime());
            jsonRst.put("power", logData.getCurrentEnergy());
            jsonRst.put("consumption", d);
            jsonRst.put("status", logData.getSsType() == 1 ? 0 : 1);


        } catch (Throwable t) {
            logger.error(Consts.MSG_CODE_E010000, t);
            throw new Exception("预期外的异常。");
        } finally {

        }
        return jsonRst;
    }


    public JSONObject getRefreshPower(EHang365StartStopReqBean param,int deviceId) throws Throwable {
        logger.info("getRefreshPower-param:" + JsonUtils.objToString(param));
        long projectId = 20200009L;
        // 每个子项目设备只有1台
        //int deviceId = 1;
        Lock lock = new Lock();
        JSONObject jsonRst = new JSONObject();
        ProjectConfig projectConfig = projectConfigDao.findProjectConfigByProjectIdNoCache(projectId);
        @SuppressWarnings("unchecked")
        Map<String, Long> eqptMapping = (Map<String, Long>) projectConfig.getConfig().get("eqptMapping");
//    Long acconeId = eqptMapping.get(param.getEquipmentId());//getOder
        Long acconeId = eqptMapping.get(param.getPowerOutletId());//getOder 岸电设备插口ID（指厂商在岸电设备管理平台新增设备后生成的插口ID）
        if (acconeId == null) {
            throw new EmsBisException("设备不存在。");
        }
        Map<String, Integer> qRcodeDeviceId = (Map<String, Integer>) projectConfig.getConfig().get("qRCodeDeviceId");
        if (qRcodeDeviceId.containsKey(param.getPowerOutletId())) {
            deviceId = qRcodeDeviceId.get(param.getPowerOutletId());
        }

        // TODO:判断是否已经在充电
        ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
        if (logData == null) {
            throw new EmsBisException("订单不存在");
        }
        if (logData.getSsType() == 2) {
            jsonRst.put("time", logData.getDateTime());
            jsonRst.put("power", logData.getCurrentEnergy());
            jsonRst.put("consumption", 0);
            jsonRst.put("status", 1);
            return jsonRst;
        }
        Double c = 0.0;
        TDevice tDevice = new TDevice();
        tDevice.setDeviceId(deviceId);
        tDevice.setAcconeId(acconeId);
        TDevice device = deviceDao.findByCondition(tDevice);
        if (device != null) {
            Long deviceNo = device.getDeviceNo(); //"TPAE"
            RealDataItem realDataItem =
                    (RealDataItem) realDataRedisDao.getRealData(deviceNo, "TPAE");
            if (realDataItem != null) {
                ArrayList<Double> list = (ArrayList<Double>) realDataItem.getData();
                if (list != null) {
                    c = list.get(0) - logData.getCurrentEnergy();
                }
            }
        }
        if (c < 0) {
            c = 0.0;
        }
        jsonRst.put("time", logData.getDateTime());
        jsonRst.put("power", logData.getCurrentEnergy());
        jsonRst.put("consumption", c);
        jsonRst.put("status", logData.getSsType() == 1 ? 0 : 1);

        return jsonRst;
    }

    public JSONObject startUseElec(EHang365StartStopReqBean param) throws Throwable {
        logger.info("startUseElec-param:" + JsonUtils.objToString(param));
        TDevice device = deviceDao.findByAnDianId(param.getPowerOutletId());
        if (device == null) {
            //江苏亨通
            return startUseElec(param, 1, null);
        } else {
            //国信1#、2#以及其他

            return startUseElec2(param, device);
        }
    }

    public JSONObject startUseElec2(EHang365StartStopReqBean param, TDevice device) throws Throwable {
        JSONObject jsonRst = new JSONObject();
        try {
            //判断是否正在用电
            ShorePowerLogData shorePowerLogData = shorePowerLogDataDao.findLastByAcconeDevice(device.getAcconeId(), device.getDeviceId());
            if (shorePowerLogData != null && shorePowerLogData.getSsType() == Consts.EHANG_SS_TYPE_START) {
                throw new EmsBisException("正在用电。");//正在用电
            }

            boolean isOnline = false;
            double energy = 0.0d;
            RealDataItem realDataItem =
                    (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "D_TE");
            if (realDataItem != null) {
                Date realDate =
                        DateTimeUtils
                                .parseDate(realDataItem.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
                long second = DateTimeUtils.differFromSecond(realDate, Calendar.getInstance().getTime());

                // 5分钟之内的值均可信
                if (second < 300) {
                    isOnline = true;
                    energy = Double.parseDouble(realDataItem.getData().toString());
                }
            }
            if (isOnline == false) {
                throw new EmsBisException("设备不在线。");
            }
            ShorePowerLogData logData = new ShorePowerLogData();
            logData.setAcconeId(device.getAcconeId());
            logData.setDeviceId(device.getDeviceId());
            logData.setSsType(Consts.EHANG_SS_TYPE_START);
            logData.setSubProjectId(device.getSubProjectId());
            logData.setProjectId(0);
            logData.setTimestamp(DateTimeUtils.getCurrentLongTime());
            logData.setStartTime(DateTimeUtils.getCurrentLongTime());
            logData.setDateTime(DateTimeUtils.dataTimeLongToString(DateTimeUtils.getCurrentLongTime()));
            logData.setCurrentEnergy(energy);
            logData.setStartEnergy(energy);
            logData.setPowerOutletId(param.getPowerOutletId());
            logData.setOrderId(param.getOrderId());
            shorePowerLogDataDao.save(logData);
            jsonRst.put("time", DateTimeUtils.dataTimeLongToString(logData.getStartTime()));
            jsonRst.put("power", energy);
            jsonRst.put("consumption", 0);
            jsonRst.put("status", 0);

        } catch (Throwable t) {
            logger.error(Consts.MSG_CODE_E010000, t);
            throw t;
        } finally {

        }
        return jsonRst;
    }


    public JSONObject startUseElec(EHang365StartStopReqBean param, Integer deviceId, Long acconeId) throws Throwable {
        long projectId = 20200009L;
        // 每个子项目设备只有1台

        Lock lock = new Lock();
        JSONObject jsonRst = new JSONObject();
        ProjectConfig projectConfig = projectConfigDao.findProjectConfigByProjectIdNoCache(projectId);
        @SuppressWarnings("unchecked")
        Map<String, Long> eqptMapping = (Map<String, Long>) projectConfig.getConfig().get("eqptMapping");
//    Long acconeId = eqptMapping.get(param.getEquipmentId());//getOder
        if (acconeId == null) {
            acconeId = eqptMapping.get(param.getPowerOutletId());//getOder 岸电设备插口ID（指厂商在岸电设备管理平台新增设备后生成的插口ID）
            Map<String, Integer> qRcodeDeviceId = (Map<String, Integer>) projectConfig.getConfig().get("qRCodeDeviceId");
            if (qRcodeDeviceId.containsKey(param.getPowerOutletId())) {
                deviceId = qRcodeDeviceId.get(param.getPowerOutletId());
            }
        }
        if (acconeId == null) {
            throw new EmsBisException("设备不存在。");
        }
        ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
        if (logData != null && logData.getSsType() == Consts.EHANG_SS_TYPE_START) {
            throw new EmsBisException("正在用电。");
        }
        // 设置锁，如果已有同样指令锁，则返回false
        boolean setLockFlag = eHang365Listener.putLockerMap(acconeId, deviceId, lock);
        if (setLockFlag == false) {
            throw new EmsBisException("已有指令在执行中。");
        }
        Ehang365StartStopBean sendData = new Ehang365StartStopBean();
        sendData.setSendNo(1);
        sendData.setDeviceId(deviceId);
        sendData.setAcconeId(acconeId);

        rabbitTemplate.convertAndSend(ehang365EqptStartExchangeName, ehang365EqptStartRequestRoutingKeyName,
                sendData, new CorrelationData());
        try {
//      synchronized (lock) {
//        lock.wait(timeout);
//      }
            boolean fig = false;//是否成功
            Thread.currentThread().sleep(2000);
            for (int i = 0; i < 5; i++) {
                ShorePowerLogData laseLogData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
                if (laseLogData.getSsType() == Consts.EHANG_SS_TYPE_START) {
                    try {
                        if (param.getOrderId() != null || param.getPowerOutletId() != null) {
                            shorePowerLogDataDao.update(acconeId, deviceId, param.getOrderId(), param.getPowerOutletId());
                        }

                    } catch (Exception ex) {
                        logger.error("岸电用电写入orderId错误：", ex);
                    }
                    jsonRst.put("time", DateTimeUtils.dataTimeLongToString(laseLogData.getTimestamp()));
                    jsonRst.put("power", laseLogData.getStartEnergy());
                    jsonRst.put("consumption", 0);
                    jsonRst.put("status", 0);
                    fig = true;
                    i = 5;

                } else {
                    Thread.currentThread().sleep(2000);
                }
            }
            if (fig == false) {
                throw new EmsBisException("用电异常");
            }

        } catch (EmsBisException ee) {
            logger.warn(ee.getMessage(), ee);
            throw new Exception(ee.getMessage());
        } catch (Throwable t) {
            logger.error(Consts.MSG_CODE_E010000, t);
            throw t;
        } finally {
            eHang365Listener.removeLockerMap(acconeId, deviceId);
        }
        return jsonRst;
    }

    public JSONObject stopUseElec(EHang365StartStopReqBean param) throws Throwable {
        logger.info("stopUseElec-param:" + JsonUtils.objToString(param));
        TDevice device = deviceDao.findByAnDianId(param.getPowerOutletId());
        if (device == null) {
            //江苏亨通
            return stopUseElec(param, 1, null);
        } else {
            //国信1#、2#以及其他

            return stopUseElec2(param, device);
        }

    }

    public JSONObject stopUseElec(EHang365StartStopReqBean param, Integer deviceId, Long acconeId) throws Throwable {
        long projectId = 20200009L;
        // 每个子项目设备只有1台
        Lock lock = new Lock();
        JSONObject jsonRst = new JSONObject();
        ProjectConfig projectConfig = projectConfigDao.findProjectConfigByProjectIdNoCache(projectId);
        @SuppressWarnings("unchecked")
        Map<String, Long> eqptMapping = (Map<String, Long>) projectConfig.getConfig().get("eqptMapping");
//    Long acconeId = eqptMapping.get(param.getEquipmentId());
        if (acconeId == null) {
            acconeId = eqptMapping.get(param.getPowerOutletId());
            Map<String, Integer> qRcodeDeviceId = (Map<String, Integer>) projectConfig.getConfig().get("qRCodeDeviceId");
            if (qRcodeDeviceId.containsKey(param.getPowerOutletId())) {
                deviceId = qRcodeDeviceId.get(param.getPowerOutletId());
            }
        }
        if (acconeId == null) {
            throw new EmsBisException("设备不存在。");
        }

        ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
        if (logData != null && logData.getSsType() == Consts.EHANG_SS_TYPE_STOP) {
            //throw new EmsBisException("已经停止用电。");
            jsonRst.put("time", DateTimeUtils.dataTimeLongToString(logData.getTimestamp()));
            jsonRst.put("power", logData.getCurrentEnergy());
            jsonRst.put("consumption", logData.getDiff());
            jsonRst.put("status", 1);
            return jsonRst;
        }
        // 设置锁，如果已有同样指令锁，则返回false
        boolean setLockFlag = eHang365Listener.putLockerMap(acconeId, deviceId, lock);
        if (setLockFlag == false) {
            throw new EmsBisException("已有指令在执行中。");
        }
        Ehang365StartStopBean sendData = new Ehang365StartStopBean();
        sendData.setSendNo(1);
        sendData.setDeviceId(deviceId);
        sendData.setAcconeId(acconeId);
        rabbitTemplate.convertAndSend(ehang365EqptStopExchangeName, ehang365EqptStopRequestRoutingKeyName,
                sendData, new CorrelationData());
        try {
//      synchronized (lock) {
//        lock.wait(timeout);
//      }
            boolean fig = false;//是否成功
            Thread.currentThread().sleep(2000);
            for (int i = 0; i < 4; i++) {
                ShorePowerLogData lastLogData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
                //Ehang365StartStopBean msg = shorePowerRedisDao.getStopInfoCache(acconeId, deviceId);
                if (lastLogData.getSsType() == Consts.EHANG_SS_TYPE_STOP) {
                    jsonRst.put("time", DateTimeUtils.dataTimeLongToString(lastLogData.getTimestamp()));
                    jsonRst.put("power", lastLogData.getCurrentEnergy());
                    jsonRst.put("consumption", lastLogData.getDiff());
                    jsonRst.put("status", 1);
                    fig = true;
                    i = 4;
                } else {
                    Thread.currentThread().sleep(2000);
                }
            }
            if (fig == false) {
                throw new EmsBisException("用电异常");
            }

//            Thread.currentThread().sleep(4000);
//            // listener已经收到响应并继续执行
//            ShorePowerLogData lastLogData = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
//            //Ehang365StartStopBean msg = shorePowerRedisDao.getStopInfoCache(acconeId, deviceId);
//            if (lastLogData.getSsType() == Consts.EHANG_SS_TYPE_STOP) {
//                jsonRst.put("time", DateTimeUtils.dataTimeLongToString(lastLogData.getTimestamp()));
//                jsonRst.put("power", lastLogData.getCurrentEnergy());
//                jsonRst.put("consumption", lastLogData.getDiff());
//                jsonRst.put("status", 1);
//                // 临时方法，正式版本删除
//                //jsonRst.put("endTime", updateShoreLogDataTimeStamp(acconeId, Consts.EHANG_SS_TYPE_STOP, msg.getTimestamp()));
//            } else {
////                logger.warn(msg.getErrorMsg());
//                throw new EmsBisException("结束异常");
//            }
        } catch (InterruptedException ie) {
            logger.warn(ie.getMessage(), ie);
            throw new Exception("等待超时。");
        } catch (EmsBisException ee) {
            logger.warn(ee.getMessage(), ee);
            throw new Exception(ee.getMessage());
        } catch (Throwable t) {
            logger.error(Consts.MSG_CODE_E010000, t);
            throw new Exception("预期外的异常。");
        } finally {
            eHang365Listener.removeLockerMap(acconeId, deviceId);
        }
        return jsonRst;
    }

    public JSONObject stopUseElec2(EHang365StartStopReqBean param, TDevice device) throws Throwable {
        JSONObject jsonRst = new JSONObject();
        try {
            //判断是否正在用电
            ShorePowerLogData logData = shorePowerLogDataDao.findLastByAcconeDevice(device.getAcconeId(), device.getDeviceId());
            if (logData == null) {
                throw new EmsBisException("订单不存在");
            }
            if (logData != null && logData.getSsType() == Consts.EHANG_SS_TYPE_STOP) {
                //throw new EmsBisException("正在用电。");
                jsonRst.put("time", DateTimeUtils.dataTimeLongToString(logData.getTimestamp()));
                jsonRst.put("power", logData.getCurrentEnergy());
                jsonRst.put("consumption", logData.getDiff());
                jsonRst.put("status", 1);
                return jsonRst;
            }

            boolean isOnline = false;
            double energy = 0.0d;
            RealDataItem realDataItem =
                    (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "D_TE");
            if (realDataItem != null) {
                Date realDate =
                        DateTimeUtils
                                .parseDate(realDataItem.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
                long second = DateTimeUtils.differFromSecond(realDate, Calendar.getInstance().getTime());

                // 5分钟之内的值均可信
                if (second < 300) {
                    isOnline = true;
                    energy = Double.parseDouble(realDataItem.getData().toString());
                }
            }
            if (isOnline == false) {
                throw new EmsBisException("设备不在线。");
            }
            //ShorePowerLogData logData = new ShorePowerLogData();

            logData.setTimestamp(DateTimeUtils.getCurrentLongTime());
            logData.setDateTime(DateTimeUtils.dataTimeLongToString(DateTimeUtils.getCurrentLongTime()));
            logData.setCurrentEnergy(energy);
            Double d = ArithUtil.sub(energy, logData.getStartEnergy());
            BigDecimal b = new BigDecimal(d);
            d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logData.setDiff(d);
            logData.setSsType(Consts.EHANG_SS_TYPE_STOP);
            shorePowerLogDataDao.updateModel(logData);
            jsonRst.put("time", DateTimeUtils.dataTimeLongToString(logData.getTimestamp()));
            jsonRst.put("power", energy);
            jsonRst.put("consumption", logData.getDiff());
            jsonRst.put("status", 1);

        } catch (Throwable t) {
            logger.error(Consts.MSG_CODE_E010000, t);
            throw t;
        } finally {

        }
        return jsonRst;
    }

    public JSONObject startUseElecDummy(EHang365StartStopReqBean param) {
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        json.put("startTime", sdf.format(new Date()));
        json.put("startPower", 20.0);
        return json;
    }

    public JSONObject stopUseElecDummy(EHang365StartStopReqBean param) {
        JSONObject json = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        json.put("endTime", sdf.format(new Date()));
        json.put("endPower", 70.0);
        json.put("consumption", 50.0);
        return json;
    }

    // 临时方法
    private String updateShoreLogDataTimeStamp(long acconeId, int ssType, long timestamp) {
        List<MongoBathUpdateOptions> batchUpdateOptionDayList = new ArrayList<MongoBathUpdateOptions>();
        Update updateDay = new Update();
        long time = (new Date()).getTime();
        String dateTime = DateTimeUtils.dateLong2FString(time,
                DateTimeUtils.FORMAT_yyyy_MM_dd_HH_mm_ss);
        updateDay.set("timestamp", time);
        updateDay.set("dateTime", dateTime);
        batchUpdateOptionDayList.add(new MongoBathUpdateOptions(Query.query(Criteria.where("acconeId")
                .is(acconeId).and("ssType").is(ssType).and("timestamp").is(timestamp)), updateDay, true, true));
        shorePowerLogDataDao.batchUpdateByOptions(ShorePowerLogData.class, batchUpdateOptionDayList);
        return dateTime;
    }

}
