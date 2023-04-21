package com.shmet.handler;

import com.shmet.bean.waterMeter.*;
import com.shmet.dao.ITWaterMeterDao;
import com.shmet.dao.ITWaterMeterRecordDao;
import com.shmet.dao.IWaterMeterTaskDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mysql.gen.TWaterMeter;
import com.shmet.entity.mysql.gen.TWaterMeterRecord;
import com.shmet.entity.mysql.gen.TWaterMeterTask;
import com.shmet.helper.GuidUuidUtils;
import com.shmet.helper.JsonUtils;
import com.shmet.helper.RequestUtils;
import com.shmet.helper.redis.RedisUtil;
import org.beetl.sql.core.query.LambdaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service
public class WaterMeterHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  //@Value("${watermeter.dahua.url}")
  String wmUrl;

  //@Value("${watermeter.dahua.user}")
  String userName;

  //@Value("${watermeter.dahua.password}")
  String password;

  //@Value("${watermeter.dahua.tokenkey}")
  String tokenkey;

  @Autowired
  RedisUtil redisUtil;

  @Resource
  IWaterMeterTaskDao iWaterMeterTaskDao;

  @Resource
  ITWaterMeterRecordDao itWaterMeterRecordDao;

  @Resource
  ITWaterMeterDao itWaterMeterDao;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  RestTemplate restTemplate;

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
   * 获取Token
   */
  public String GetToken() throws Exception {
    //token存在缓存直接返回
    Object to = redisUtil.get(tokenkey, 0);
    if (to != null) {
      return (String) to;
    }

    String url = wmUrl + "/userInfo/getToken";
    Map<String, Object> map = new HashMap<>();
    map.put("userName", userName);
    map.put("pw", password);
    GetTokenInfo getTokenInfo = RequestUtils.postJson(url, map, GetTokenInfo.class);
    String token = getTokenInfo.getToken();
    //判断是否成功
    if (token == null || token.length() == 0 || getTokenInfo.getCode() == 0) {
      throw new Exception(getTokenInfo.getMsg());
    }
    int time = 60 * 60;//60分钟
    //写入缓存
    redisUtil.set(tokenkey, token, time);

    return token;

  }

  /**
   * 根据 token 和设备 id 查询余量/已使用量
   *
   * @param waterMeterBaseInfo token/meter_id
   */
  public GetRemainByIdInfo GetRemainById(WaterMeterBaseInfo waterMeterBaseInfo) throws Exception {
    String url = wmUrl + "/userInfo/getRemainById";
    Map<String, Object> map = new HashMap<>();
    map.put("token", GetToken());
    map.put("meter_id", waterMeterBaseInfo.getMeter_id());
    GetRemainByIdInfo getRemainByIdInfo = RequestUtils.postJson(url, map, GetRemainByIdInfo.class);

    //Code=0 表示token失效 判断token是否失效,出现几率非常小，以防万一
    if (getRemainByIdInfo.getCode() == 0) {
      String token = GetToken();
      if (!StringUtils.isEmpty(token)) {
        getRemainByIdInfo = RequestUtils.postJson(url, map, GetRemainByIdInfo.class);
      }
    }
    return getRemainByIdInfo;
  }

  /**
   * 根据 token 和设备 id 金额充值 金额 、唯一交易号，对某个设备 id 充值
   *
   * @param waterMeterBaseInfo amount、meter_id、token、tra_id
   */
  public PayAmountByIdInfo payAmountById(WaterMeterBaseInfo waterMeterBaseInfo) throws Exception {
    String url = wmUrl + "/userInfo/payAmountById";
    Map<String, Object> map = new HashMap<>();
    map.put("token", GetToken());
    map.put("meter_id", waterMeterBaseInfo.getMeter_id());
    map.put("amount", waterMeterBaseInfo.getAmount());
    map.put("tra_id", waterMeterBaseInfo.getTra_id());
    PayAmountByIdInfo payAmountByIdInfo = RequestUtils.postJson(url, map, PayAmountByIdInfo.class);

    //Code=0 表示token失效 判断token是否失效,出现几率非常小，以防万一
    if (payAmountByIdInfo.getCode() == 0) {
      String token = GetToken();
      if (!StringUtils.isEmpty(token)) {
        payAmountByIdInfo = RequestUtils.postJson(url, map, PayAmountByIdInfo.class);
      }
    }

    return payAmountByIdInfo;

  }


  /**
   * 根据 token 和设备 id 和交易号，查询充值结果
   */
  public GetPayResultByIdInfo getPayResultById(WaterMeterBaseInfo waterMeterBaseInfo) throws Exception {
    String url = wmUrl + "/userInfo/getPayResultById";
    Map<String, Object> map = new HashMap<>();
    map.put("token", GetToken());
    map.put("meter_id", waterMeterBaseInfo.getMeter_id());
    map.put("tra_id", waterMeterBaseInfo.getTra_id());
    GetPayResultByIdInfo getPayResultByIdInfo = RequestUtils.postJson(url, map, GetPayResultByIdInfo.class);

    //Code=0 表示token失效 判断token是否失效,出现几率非常小，以防万一
    if (getPayResultByIdInfo.getCode() == 0) {
      String token = GetToken();
      if (!StringUtils.isEmpty(token)) {
        getPayResultByIdInfo = RequestUtils.postJson(url, map, GetPayResultByIdInfo.class);
      }
    }

    return getPayResultByIdInfo;
  }

  /**
   * 控制水阀
   */
  public WaterMeterBaseInfo controlIntefer(WaterMeterBaseInfo waterMeterBaseInfo, Integer open_close) throws Exception {
    String url = wmUrl + "/userInfo/getPayResultById";
    Map<String, Object> map = new HashMap<>();
    map.put("token", GetToken());
    map.put("meter_id", waterMeterBaseInfo.getfWaterMeterNumber());
    map.put("tra_id", waterMeterBaseInfo.getTra_id());
    map.put("open_close", open_close);
    //open_close=1--无条件送电, open_close=2--无条件断电 ,open_close=3--剩余不够断电、
    //open_close=4--剩余不够不断电;
    WaterMeterBaseInfo model = RequestUtils.postJson(url, map, WaterMeterBaseInfo.class);
    //Code=0 表示token失效 判断token是否失效,出现几率非常小，以防万一
    if (model.getCode() == 0) {
      String token = GetToken();
      if (!StringUtils.isEmpty(token)) {
        model = RequestUtils.postJson(url, map, WaterMeterBaseInfo.class);
      }
    }
    return model;
  }

  /**
   * 根据 token 和设备 id 和交易号，表计剩余量清零
   */
  public ClearInteferInfo clearIntefer(WaterMeterBaseInfo waterMeterBaseInfo) throws Exception {
    String url = wmUrl + "/userInfo/clearIntefer";
    Map<String, Object> map = new HashMap<>();
    map.put("token", GetToken());
    map.put("meter_id", waterMeterBaseInfo.getMeter_id());
    map.put("tra_id", waterMeterBaseInfo.getTra_id());
    ClearInteferInfo clearInteferInfo = RequestUtils.postJson(url, map, ClearInteferInfo.class);

    //Code=0 表示token失效 判断token是否失效,出现几率非常小，以防万一
    if (clearInteferInfo.getCode() == 0) {
      String token = GetToken();
      if (!StringUtils.isEmpty(token)) {
        clearInteferInfo = RequestUtils.postJson(url, map, ClearInteferInfo.class);
      }
    }

    return clearInteferInfo;
  }

  /**
   * 根据 水表号 得到所有设备的余量/已使用量
   */
  public List<TWaterMeter> getAllWaterMeter(TWaterMeter tWaterMeter) {

    LambdaQuery<TWaterMeter> query = itWaterMeterDao.createLambdaQuery();
    //如果有就根据水表号查询，没有查询所有
    if (tWaterMeter != null && !StringUtils.isEmpty(tWaterMeter.getfWaterMeterNumber())) {
      query.andEq(TWaterMeter::getfWaterMeterNumber, tWaterMeter.getfWaterMeterNumber());
    }

    return query.select();
  }

  /**
   * 新增预充值
   */
  public Result insertWaterMeterTask(WaterMeterTaskInfo waterMeterTaskInfo) {
    Result result = Result.getSuccessResultInfo();

    //判断参数是否正确
    if (waterMeterTaskInfo == null || waterMeterTaskInfo.getfWaterMeterNumber().size() == 0 ||
        waterMeterTaskInfo.getfRechargeAmount() == 0) {
      result.setSuccess(false);
      result.setMessage("充值参数错误!");
      return result;
    }
    int count = 0;
    for (Integer i : waterMeterTaskInfo.getfWaterMeterNumber()) {

      Date date = new Date();
      TWaterMeterTask tWaterMeterTask = new TWaterMeterTask();
      tWaterMeterTask.setfStatus("1");//1待生效状态
      tWaterMeterTask.setfIsReset(waterMeterTaskInfo.getfIsReset());
      tWaterMeterTask.setfRechargeAmount(waterMeterTaskInfo.getfRechargeAmount());
      tWaterMeterTask.setfValidTime(waterMeterTaskInfo.getfValidTime());
      tWaterMeterTask.setfCreateTime(date);
      tWaterMeterTask.setfWaterMeterNumber(i);
      tWaterMeterTask.setfProcessStatus("0");
      //没找到事务怎么用，先直接一条条新增

      //
      iWaterMeterTaskDao.insert(tWaterMeterTask);


      count++;

    }
    result.setData(count);
    result.setMessage("新增成功");

    return result;
  }

  /**
   * 分页查询充值任务
   */
  public ResultSearch queryWaterMeter(TWaterMeterTaskInfo tWaterMeterTaskInfo) {

    Integer pageNo = tWaterMeterTaskInfo.getPageNo();
    Integer pageSize = tWaterMeterTaskInfo.getPageSize();

    if (pageNo == 1) {
      pageNo = 1;
    } else {
      pageNo = (pageNo - 1) * pageSize + 1;
    }

    LambdaQuery<TWaterMeterTask> query = iWaterMeterTaskDao.createLambdaQuery();
    LambdaQuery<TWaterMeterTask> queryCount = iWaterMeterTaskDao.createLambdaQuery();

    List<TWaterMeterTask> list;
    if (!StringUtils.isEmpty(tWaterMeterTaskInfo.getfStatus())) {
      query.andEq(TWaterMeterTask::getfStatus, tWaterMeterTaskInfo.getfStatus());
      queryCount.andEq(TWaterMeterTask::getfStatus, tWaterMeterTaskInfo.getfStatus());
    }
    if (!StringUtils.isEmpty(tWaterMeterTaskInfo.getfWaterMeterNumber())) {
      query.andEq(TWaterMeterTask::getfWaterMeterNumber, tWaterMeterTaskInfo.getfWaterMeterNumber());
      queryCount.andEq(TWaterMeterTask::getfWaterMeterNumber, tWaterMeterTaskInfo.getfWaterMeterNumber());
    }

    // 数据的总行数
    long count = queryCount.count();

    //查询列表
    list = query.limit(pageNo, pageSize).desc(TWaterMeterTask::getfId).select();


    ResultSearch resultSearch = ResultSearch.getSuccessResultInfo(list);
    resultSearch.setTotalCount(count);
    resultSearch.setPageNo(pageNo);
    resultSearch.setPageSize(pageSize);
    return resultSearch;
  }

  /**
   * 取消预充值
   */
  public Result cancelTask(List<Integer> list) {
    Result result = Result.getSuccessResultInfo();
    if (list == null || list.size() == 0) {
      result.setSuccess(false);
      result.setMessage("参数错误");
      return result;
    }

    int count = 0;
    for (Integer id : list) {
      TWaterMeterTask tWaterMeterTask = new TWaterMeterTask();
      tWaterMeterTask.setfId(id);
      tWaterMeterTask.setfStatus("3");
      int i = iWaterMeterTaskDao.updateTemplateById(tWaterMeterTask);
      if (i > 0) {
        count++;
      }

    }
    result.setMessage("更新成功");
    result.setData(count);
    return result;
  }

  /**
   * 打开水表
   */
  public Result open(WaterMeterBaseInfo waterMeterBaseInfo) throws Exception {
    Result result = Result.getSuccessResultInfo();
    if (waterMeterBaseInfo == null || StringUtils.isEmpty(waterMeterBaseInfo.getfWaterMeterNumber())) {
      result.setMessage("参数不正确!");
      result.setSuccess(false);
      return result;
    }

    //判断是否有剩余水量
    TWaterMeter tWaterMeter = itWaterMeterDao.createLambdaQuery()
        .andEq(TWaterMeter::getfWaterMeterNumber, waterMeterBaseInfo.getfWaterMeterNumber())
        .single();

    if (tWaterMeter == null || tWaterMeter.getfWaterYield() <= 0) {
      result.setMessage("水量已用完，无法开阀，请到前台充值!");
      result.setSuccess(false);
      return result;
    }

    //判断水表是否正在打开
    LambdaQuery<TWaterMeterRecord> query = itWaterMeterRecordDao.createLambdaQuery();
    List<TWaterMeterRecord> list = query.andEq(TWaterMeterRecord::getfWaterMeterNumber, waterMeterBaseInfo.getfWaterMeterNumber())
        .andEq(TWaterMeterRecord::getfStatus, "1")
        .andEq(TWaterMeterRecord::getfType, "1")
        .select();
    if (list.size() > 0) {
      result.setMessage("水表正在开阀，不要重复提交!");
      result.setSuccess(false);
      return result;
    }

    waterMeterBaseInfo.setTra_id(GuidUuidUtils.getGuidorUuid());

    //open_close=1--无条件送电, open_close=2--无条件断电 ,open_close=3--剩余不够断电、
    //open_close=4--剩余不够不断电;
    WaterMeterBaseInfo model = controlIntefer(waterMeterBaseInfo, 1);
    result.setMessage(model.getMsg());
    result.setData(model.getCode());
    if (model.getCode() == 1) { //提交成功

      TWaterMeterRecord tWaterMeterRecord = new TWaterMeterRecord();
      tWaterMeterRecord.setfStatus("1");//状态：1已提交，2成功，3失败
      //tWaterMeterRecord.setfMaterMeterTaskId(model.getfId());
      tWaterMeterRecord.setfTraId(waterMeterBaseInfo.getTra_id());
      tWaterMeterRecord.setfWaterMeterNumber(waterMeterBaseInfo.getfWaterMeterNumber());
      tWaterMeterRecord.setfType("1"); //记录类型：1开阀，2关阀
      tWaterMeterRecord.setfCreateTime(new Date());
      itWaterMeterRecordDao.insert(tWaterMeterRecord);

    } else {
      result.setSuccess(false);
    }

    return result;
  }

  public void waterMeterTaskJob() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    //分三步走：
    //第一步：处理充值
    //第二步：处理结果
    //第三步：开始充值
    //开始处理第一步
    LambdaQuery<TWaterMeterTask> query = iWaterMeterTaskDao.createLambdaQuery();
    Calendar nowTime = Calendar.getInstance();

    List<TWaterMeterTask> list =
        query.andEq(TWaterMeterTask::getfStatus, "1")
            .andLessEq(TWaterMeterTask::getfValidTime, df.format(nowTime.getTime()))
            .select();

    if (list != null && list.size() > 0) {
      for (TWaterMeterTask model : list) {
        TWaterMeter tWaterMeter = itWaterMeterDao.createLambdaQuery().andEq(TWaterMeter::getfWaterMeterNumber, model.getfWaterMeterNumber()).single();
        Integer rechargeAmount = tWaterMeter.getfWaterYield();
        if (model.getfIsReset() == 1) {
          //清零
          rechargeAmount = 0;
        }
        //加上新充值的水量
        rechargeAmount = rechargeAmount + model.getfRechargeAmount();
        //重新充值水量
        tWaterMeter.setfWaterYield(rechargeAmount);
        if (itWaterMeterDao.updateTemplateById(tWaterMeter) > 0) {
          model.setfStatus("2");
          model.setfProcessStatus("2");
          iWaterMeterTaskDao.updateTemplateById(model);
        }
      }
    }
    //第一步处理结束
    //开始处理第二步 获取 开阀、关阀 记录
    LambdaQuery<TWaterMeterRecord> queryRecord = itWaterMeterRecordDao.createLambdaQuery();
    //状态：1已提交，2成功，3失败
    List<TWaterMeterRecord> listRecord = queryRecord.andEq(TWaterMeterRecord::getfStatus, "1").select();
    if (listRecord != null && listRecord.size() > 0) {
      for (TWaterMeterRecord model : listRecord) {
        this.rabbitTemplate.convertAndSend(recordtaskExchangeName, recordtaskRoutingKey, model,
            new CorrelationData());
      }
    }
    //第二步处理结束
    //开始处理第三步 处理关阀，半小时自动关阀
    String time = "30";//多久自动关水
    String sql = "select * from t_water_meter where f_last_open_time>f_last_close_time and date_add(f_last_open_time, interval " + time + " minute)>=now()";
    List<TWaterMeter> waterMeterList = itWaterMeterDao.execute(sql);
    if (waterMeterList != null && waterMeterList.size() > 0) {
      for (TWaterMeter waterMeter : waterMeterList) {
        this.rabbitTemplate.convertAndSend(canceltaskExchangeName, canceltaskRoutingKey, waterMeter,
            new CorrelationData());
      }
    }

    //第三步处理结束 处理关阀，半小时自动关阀
//        //开始处理第三步
//        LambdaQuery<TWaterMeterTask> lambdaQuery= iWaterMeterTaskDao.createLambdaQuery();
//
//        nowTime = Calendar.getInstance();
//        nowTime.add(Calendar.MINUTE, -2);
//
//        List<TWaterMeterTask> waterMeterTaskList= lambdaQuery.andEq(TWaterMeterTask::getfStatus,"0")
//                .andLessEq(TWaterMeterTask::getfValidTime,df.format(nowTime.getTime()))
//                .select();
//
//        if(waterMeterTaskList!=null && waterMeterTaskList.size() >0){
//            for (TWaterMeterTask model:waterMeterTaskList){
//                if((model.getfIsReset()==1&&model.getfProcessStatus().equals("2"))||model.getfIsReset()==0){
//                    this.rabbitTemplate.convertAndSend(rechargetaskExchangeName, rechargetaskRoutingKey, model,
//                            new CorrelationData());
//                }
//            }
//        }
//        //第三步处理结束
  }

  /**
   * 关阀
   */
  @RabbitListener(queues = "${rabbitmq.queues.watermeter.canceltask}")
  public void cancelTaskListener(TWaterMeter model) throws Exception {
    WaterMeterBaseInfo waterMeterBaseInfo = new WaterMeterBaseInfo();
    waterMeterBaseInfo.setMeter_id(model.getfWaterMeterNumber().toString());
    waterMeterBaseInfo.setTra_id(GuidUuidUtils.getGuidorUuid());

    //open_close=1--无条件送电, open_close=2--无条件断电 ,open_close=3--剩余不够断电、
    //open_close=4--剩余不够不断电;
    WaterMeterBaseInfo wmInfo = controlIntefer(waterMeterBaseInfo, 2);

    if (wmInfo.getCode() == 1) { //提交成功
      TWaterMeterRecord tWaterMeterRecord = new TWaterMeterRecord();
      tWaterMeterRecord.setfStatus("1");//状态：1已提交，2成功，3失败
      //tWaterMeterRecord.setfMaterMeterTaskId(model.getfId());
      tWaterMeterRecord.setfTraId(waterMeterBaseInfo.getTra_id());
      tWaterMeterRecord.setfWaterMeterNumber(waterMeterBaseInfo.getfWaterMeterNumber());
      tWaterMeterRecord.setfType("2"); //记录类型：1开阀，2关阀
      tWaterMeterRecord.setfCreateTime(new Date());
      itWaterMeterRecordDao.insert(tWaterMeterRecord);

    }

  }

  /**
   * 监听处理结果
   */
  @RabbitListener(queues = "${rabbitmq.queues.watermeter.recordtask}")
  public void recordTaskListener(TWaterMeterRecord model) throws Exception {
    //监听：成功之后需要把房间水减去1吨；
    WaterMeterBaseInfo waterMeterBaseInfo = new WaterMeterBaseInfo();
    waterMeterBaseInfo.setMeter_id(model.getfWaterMeterNumber());
    waterMeterBaseInfo.setTra_id(model.getfTraId());

    GetPayResultByIdInfo getPayResultByIdInfo = getPayResultById(waterMeterBaseInfo);
    if (getPayResultByIdInfo.getCode() == 1) {
      /*
       * 说明：参数 code=0 查询失败或认证失败，
       * code=1 充值成功，code=2 充值失败（24 小时内会继续发送失败的充值数据），
       * code=3 未执行， code=4 正在执行
       * code=5 数据不存在，
       * 参数 sent_time=充值成功或失败的时间
       * 参数 tra_id=交易号
       */
      LambdaQuery<TWaterMeter> query = itWaterMeterDao.createLambdaQuery();
      TWaterMeter tWaterMeter = query.andEq(TWaterMeter::getfWaterMeterNumber, model.getfWaterMeterNumber()).single();
      if (tWaterMeter != null) {
        if (model.getfType().equals("1")) {
          //开阀，水量-1
          if (tWaterMeter.getfWaterYield() > 0) {
            tWaterMeter.setfWaterYield(tWaterMeter.getfWaterYield() - 1);
          }
          tWaterMeter.setfLastOpenTime(new Date());
        } else {
          //关阀
          tWaterMeter.setfLastCloseTime(new Date());
        }
        itWaterMeterDao.updateTemplateById(tWaterMeter);
      }

      //修改记录信息，执行成功
      model.setfUpdateTime(new Date());
      model.setfStatus("2");//状态：1已提交，2成功，3失败
      itWaterMeterRecordDao.updateTemplateById(model);

    } else if (getPayResultByIdInfo.getCode() == 3 || getPayResultByIdInfo.getCode() == 4) {
      logger.info("调用充值清零接口，正在执行，请等待：" + model.getfTraId());
    } else {
      logger.error("调用充值清零接口，失败请求code:" + getPayResultByIdInfo.getCode() + "，参数：" + JsonUtils.objToString(waterMeterBaseInfo));
    }

  }

  /**
   * 监听充值  弃用
   */
  @RabbitListener(queues = "${rabbitmq.queues.watermeter.rechargetask}")
  public void rechargeTaskListener(TWaterMeterTask model) throws Exception {
    WaterMeterBaseInfo waterMeterBaseInfo = new WaterMeterBaseInfo();
    waterMeterBaseInfo.setMeter_id(model.getfWaterMeterNumber().toString());
    waterMeterBaseInfo.setTra_id(GuidUuidUtils.getGuidorUuid());

    //充值
    PayAmountByIdInfo payAmountByIdInfo = payAmountById(waterMeterBaseInfo);

    if (payAmountByIdInfo.getCode() == 1) { //提交成功
      TWaterMeterRecord tWaterMeterRecord = new TWaterMeterRecord();
      tWaterMeterRecord.setfStatus("1");//状态：1已提交，2成功，3失败
      tWaterMeterRecord.setfMaterMeterTaskId(model.getfId());
      tWaterMeterRecord.setfTraId(waterMeterBaseInfo.getTra_id());
      tWaterMeterRecord.setfWaterMeterNumber(waterMeterBaseInfo.getMeter_id());
      tWaterMeterRecord.setfType("2"); //记录类型：1清零，2充值
      tWaterMeterRecord.setfCreateTime(new Date());
      itWaterMeterRecordDao.insert(tWaterMeterRecord);
    } else {
      logger.error("调用充值接口失败请求参数：" + JsonUtils.objToString(waterMeterBaseInfo));
    }
  }

  /**
   * 添加
   */
  public void addTWaterMeterTask(TWaterMeterTask tWaterMeterTask) {
    iWaterMeterTaskDao.insert(tWaterMeterTask);
  }

  /**
   * 修改
   */
  public int updateTWaterMeterTask(TWaterMeterTask tWaterMeterTask) {
    return iWaterMeterTaskDao.updateTemplateById(tWaterMeterTask);
  }

  /**
   * 删除
   */
  public int deleteTDevice(String fid) {
    return iWaterMeterTaskDao.deleteById(fid);
  }
}