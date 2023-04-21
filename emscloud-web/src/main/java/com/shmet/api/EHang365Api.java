package com.shmet.api;

import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.shmet.DateTimeUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.mongodb.ShorePowerLogDataDao;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.ShorePowerLogData;
import com.shmet.entity.mysql.gen.TDevice;
import com.shmet.helper.JsonUtils;
import com.shmet.helper.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.aop.UserLoginToken;
import com.shmet.bean.EHang365StartStopReqBean;
import com.shmet.bean.EHang365StartStopResBean;
import com.shmet.handler.api.EHang365Handler;

/**
 * 船E行平台数据交换API
 */
@RequestMapping("/ehang")
@RestController
public class EHang365Api {
  
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  private final ObjectMapper om = new ObjectMapper();
  
  private final Map<String, Integer> _ERR_CODE_MSG_MAP = new HashMap<String, Integer>() {
    /**
     * 
     */
    private static final long serialVersionUID = -8390806790510505475L;

    {
      put("缺少必填参数。", 400);
      put("设备不存在。", 401);
      put("正在用电。", 402);
      put("已经停止用电。", 403);
      put("已有指令在执行中。", 404);
      put("等待超时。", 405);
      put("设备不在线。", 406);
      put("设备执行指令失败。", 407);
      put("预期外的异常。", 408);
    }
  };
  
  @Autowired
  EHang365Handler eHang365Service;

  @Autowired
  ShorePowerLogDataDao shorePowerLogDataDao;

  @Autowired
  TDeviceDao tDeviceDao;

  @Autowired
  RealDataRedisDao realDataRedisDao;

  @ResponseBody
  //@UserLoginToken
  @PostMapping(path = "/power/getStartPower", consumes="application/json")
  public Result getStartPower(HttpServletRequest request, @RequestBody EHang365StartStopReqBean param) {
    Result result=Result.getSuccessResultInfo();
    try {
      logger.info("getStartPower:"+ JsonUtils.objToString(param));
      JSONObject data  = eHang365Service.startUseElec(param);
      result.setCode("200");
      result.setData(data);
    } catch (Throwable t) {
      setAndLogErrorObject(t, result);
      JSONObject jsonObject=new JSONObject();
      jsonObject.put("result",result.getMessage());
      result.setCode("999");
      result.setData(jsonObject);
      result.setSuccess(true);
    }
    logger.info("getStartPower result:"+JsonUtils.objToString(result));
    return  result;
  }

  @ResponseBody
  @UserLoginToken
  @PostMapping(path = "/power/getEndPower", consumes="application/json")
  // @RequestMapping("/power/getEndPower")
  public Result getEndPower(HttpServletRequest request, @RequestBody EHang365StartStopReqBean param) {
    Result result=Result.getSuccessResultInfo();
    try {
      logger.info("getEndPower:"+ JsonUtils.objToString(param));
      JSONObject data = null; data = eHang365Service.stopUseElec(param);
      result.setCode("200");
      result.setData(data);
    } catch (Throwable t) {
      setAndLogErrorObject(t, result);
      JSONObject jsonObject=new JSONObject();
      jsonObject.put("result",result.getMessage());
      result.setCode("999");
      result.setData(jsonObject);
      result.setSuccess(true);
    }

    logger.info("getEndPower result:"+JsonUtils.objToString(result));
    return  result;
  }

  @ResponseBody
  @UserLoginToken
  @PostMapping(path = "/power/getRefreshPower", consumes="application/json")
 // @RequestMapping("/power/getRefreshPower")
  public Result getRefreshPower(HttpServletRequest request, @RequestBody EHang365StartStopReqBean param) {
    Result result=Result.getSuccessResultInfo();
    try {
      logger.info("getRefreshPower:"+ JsonUtils.objToString(param));
      JSONObject data = null;
      data = eHang365Service.getRefreshPower(param);
      result.setCode("200");
      result.setData(data);
    } catch (Throwable t) {
      setAndLogErrorObject(t, result);
      JSONObject jsonObject=new JSONObject();
      jsonObject.put("result",result.getMessage());
      result.setCode("999");
      result.setData(jsonObject);
      result.setSuccess(true);
    }

    logger.info("getRefreshPower result:"+JsonUtils.objToString(result));
    return  result;
  }


    /**
     * 岸电设备用电开始/结束接口
     *
     * @param request 通用请求
     * @param param 请求参数
     * @return EHang365StartStopResBean
     */
  @UserLoginToken
  @PostMapping(path = "/eqpt/ssc", consumes="application/json")
  public EHang365StartStopResBean startOrStop(HttpServletRequest request, @RequestBody EHang365StartStopReqBean param) {
    EHang365StartStopResBean rst = new EHang365StartStopResBean();
    rst.setCode(200);
    JSONObject data = null;
    //getOrder 值为1为开始用电，值为2为结束用电
    if (param.getOrder() != null && param.getOrder().equals(1)) {
      try {
        data = eHang365Service.startUseElec(param);
        rst.setData(data);
      } catch (Throwable t) {
        setAndLogErrorObject(t, rst);
      }
    } else if (param.getOrder() != null && param.getOrder().equals(2)) {
      try {
        data = eHang365Service.stopUseElec(param);
        rst.setData(data);
      } catch (Throwable t) {
        setAndLogErrorObject(t, rst);
      }
    } else if (param.getOrder() != null && param.getOrder().equals(11)) {
      try {
        data = eHang365Service.startUseElecDummy(param);
        rst.setData(data);
      } catch (Throwable t) {
        setAndLogErrorObject(t, rst);
      }
    } else if (param.getOrder() != null && param.getOrder().equals(22)) {
      try {
        data = eHang365Service.stopUseElecDummy(param);
        rst.setData(data);
      } catch (Throwable t) {
        setAndLogErrorObject(t, rst);
      }
    } else {
      rst.setCode(999);
      rst.setMsg("缺少必填参数。");
      try {
        logger.warn(om.writeValueAsString(rst));
      } catch (JsonProcessingException e) {
        logger.trace(e.getMessage(), e);
      }
    }
    return rst;
  }
  
  private void setAndLogErrorObject (Throwable t, EHang365StartStopResBean rst) {
    String msg = t.getMessage();
    Integer code = _ERR_CODE_MSG_MAP.get(msg);
    if (code == null) {
      code = 500;
      msg = "错误信息不在预定义的错误列表中，原始错误信息：[" + msg + "]";
    }
    rst.setCode(code);
    rst.setMsg(msg);
    try {
      logger.error(om.writeValueAsString(rst), t);
    } catch (JsonProcessingException e) {
      logger.trace(e.getMessage(), e);
    }
    
  }
  public void setAndLogErrorObject (Throwable t, Result rst) {
    String msg = t.getMessage();
    Integer code = _ERR_CODE_MSG_MAP.get(msg);
    if (code == null) {
      code = 999;
      msg = "用电执行失败！";
    }
    rst.setSuccess(false);
    rst.setCode(code.toString());
    rst.setMessage(msg);
    try {
      logger.error(om.writeValueAsString(rst), t);
    } catch (JsonProcessingException e) {
      logger.trace(e.getMessage(), e);
    }

  }

  @ResponseBody
  @UserLoginToken
  @PostMapping(path = "/power/compelStop", consumes="application/json")
  public Result compelStop(@RequestBody JSONObject jsonObject) {
    //智能岸电设备技术对接文档”中第四个接口，即设备异常，回调接口，由于岸电服务不再使用ehang365域名，
    // 请各厂商将“https://ehang365.cn/adback/renren-fast/updateCharging/updateStatus”，修改为“http://36.156.155.131:8090/andianbackb/renren-fast/updateCharging/updateStatus
    try {
      //自动结束用电
      Long acconeId=jsonObject.getLong("acconeId");
      Integer deviceId=jsonObject.getInteger("deviceId");
      JSONObject jsonRst = new JSONObject();

        ShorePowerLogData s = shorePowerLogDataDao.findLastByAcconeDevice(acconeId, deviceId);
        if (s == null ) {
          return  Result.getErrorResultInfo("订单不存在！");
        }
        if ( s.getSsType() == 2) {
          return Result.getErrorResultInfo("订单已结束！");
        }
        //表示没有
        Double TPAE = -1.0;
        //如果没找到设备编号,为了兼容设备点位编码规则问题
        TDevice tDevice = new TDevice();
        tDevice.setAcconeId(s.getAcconeId());
        tDevice.setDeviceId(s.getDeviceId());
        //tDevice.setDeviceModel();
        tDevice.setSubProjectId(s.getSubProjectId());
        TDevice device = tDeviceDao.findByCondition(tDevice);
        if (device != null) {
          Calendar today = Calendar.getInstance();
          if(s.getSubProjectId().longValue()==20200009L) {//亨通
            RealDataItem realDataItemCA =
                    (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "CA");
            if (realDataItemCA != null) {
              Date realDate =
                      DateTimeUtils
                              .parseDate(realDataItemCA.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
              long second = DateTimeUtils.differFromSecond(realDate, today.getTime());
              RealDataItem realDataItemTPAE =
                      (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "TPAE");//总电能
              if (realDataItemTPAE != null) {
                TPAE = ((ArrayList<Double>) realDataItemTPAE.getData()).get(0);
              }
            }}else {
              //国信
              RealDataItem realDataItem =
                      (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "D_TE");
              if (realDataItem != null) {
                  TPAE = Double.parseDouble(realDataItem.getData().toString());
//                }
              }
          }

          if (TPAE != -1.0) {
            //给汇海推送关闭
            String url = "http://36.156.155.131:8090/andianbackb/renren-fast/updateCharging/updateStatus";
            Map<String, Object> map = new HashMap();
            //map.add("recordId", "");
            map.put("powerOutletId", s.getPowerOutletId());
            map.put("recordId", s.getOrderId());
            map.put("endPower", TPAE);
            Double d = TPAE - s.getStartEnergy();
            BigDecimal b = new BigDecimal(d);
            d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            map.put("consumption", d);
            try {
              logger.info("岸电回调参数：" + JsonUtils.objToString(map));
              JSONObject object = RequestUtils.postJson(url, map, JSONObject.class);
              logger.info("岸电回调结果：" + JsonUtils.objToString(object));
              if (object.getString("code").equals("0")) {
                //修改状态为用电结束
                s.setSsType(2);
                s.setDiff(d);
                //s.setStartEnergy(s.getCurrentEnergy());
                s.setCurrentEnergy(TPAE);
                s.setTimestamp(DateTimeUtils.dataTimeToLong(new Date()));
                s.setDateTime(DateTimeUtils.dataTimeLongToString(s.getTimestamp()));
                shorePowerLogDataDao.updateModel(s);
                return  Result.getSuccessResultInfo("岸电回调成功,订单已结束");
              } else {
                logger.info("岸电回调结果失败：" + JsonUtils.objToString(object));
                return  Result.getErrorResultInfo("岸电回调结果失败："+JsonUtils.objToString(object));

              }
            } catch (Exception ex) {
              logger.error("岸电回调结果失败：", ex);
              return Result.getErrorResultInfo("操作失败！"+ex.getMessage());
            }
          }else{
            return  Result.getErrorResultInfo("未获取到有效总电能！");
          }
        }else{
          return Result.getErrorResultInfo("没有找到设备！");
        }

    } catch (Exception ex) {
      return Result.getErrorResultInfo("操作失败！"+ex.getMessage());
    }
  }
}
