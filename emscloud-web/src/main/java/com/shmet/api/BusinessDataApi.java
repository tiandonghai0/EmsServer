package com.shmet.api;

import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shmet.DateTimeUtils;
import com.shmet.aop.UserLoginToken;
import com.shmet.bean.StatisticsFormBean;
import com.shmet.bean.StatisticsResponseBean;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.utils.DateUtils;
import com.shmet.utils.RequestContextUtils;
import com.shmet.entity.dto.Result;
import com.shmet.handler.api.BusinessDataService;
import com.shmet.handler.v2.DeviceService;
import com.shmet.handler.api.TOnOffNetworkRecordService;
import com.shmet.handler.v2.UserService;
import com.shmet.vo.ChargeListDataVo;
import com.shmet.vo.UpdatePwdVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 几个核心业务controller
 */
@RequestMapping("/api")
@RestController
public class BusinessDataApi {

  @Autowired
  private BusinessDataService businessDataService;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  TOnOffNetworkRecordService tOnOffNetworkRecordService;

  @Autowired
  private UserService userService;

  @Autowired
  DeviceRealDataDao deviceRealDataDao;

  /**
   * 电量曲线相关接口
   *
   * @param callDate 格式为年份 格式必须是 yyyy
   * @return 通用处理结果
   */
  @UserLoginToken
  @RequestMapping(value = "/listElecGridStatic")
  public Result listElecGridStatic(@RequestParam String callDate) {
    List<ChargeListDataVo> chargeListDataVos = businessDataService.listElecGridStatic(callDate);
    return Result.getSuccessResultInfo(chargeListDataVos, "获取电量表格数据成功");
  }

  /**
   * 柏树 电量曲线 请求接口
   *
   * @param request 请求体
   * @return JSONObject
   */
  @GetMapping("/listElecStatic")
  public JSONObject listElecStatic(HttpServletRequest request) {
    JSONObject param = RequestContextUtils.readJson(request);
    String endTime = param.getString("endTime");
    return businessDataService.listElecStatic(endTime);
  }

  /**
   * 告警历史数据 请求接口
   *
   * @param request 请求体
   * @return 通用处理结果
   */
  @UserLoginToken
  @RequestMapping(value = "listHistoryData")
  public Result listHistoryData(HttpServletRequest request) {
    //从请求体中解析出json数据
    JSONObject param = RequestContextUtils.readJson(request);
    return Result.getSuccessResultInfo(businessDataService.listHistoryData(param), "获取功率曲线数据成功");
  }

  /**
   * 故障告警统一接口 其中 告警事件 / 故障事件通过 alarmFlag 进行区分
   *
   * @return 通用处理结果
   */
  @UserLoginToken
  @RequestMapping(value = "/listAlarmData")
  public Result listAlarmData(HttpServletRequest request) {
    JSONObject param = RequestContextUtils.readJson(request);
    String subProjectId = param.getString("subProjectId");
    if (StringUtils.isBlank(subProjectId)) {
      return Result.getErrorResultInfo("subProjectId不能为空");
    }
    return Result.getSuccessResultInfo(businessDataService.listAlarmData(param), "获取告警历史数据成功");
  }

  /**
   * 投资收益中的统计分析
   *
   * @return 通用处理结果
   */
  @UserLoginToken
  @RequestMapping("/incomeHourStatis")
  public Result incomeHourStatis(HttpServletRequest request) {
    JSONObject param = RequestContextUtils.readJson(request);
    return Result.getSuccessResultInfo(businessDataService.incomeHourStatis(param), "获取统计数据成功,可以用来数据分析");
  }

  /**
   * 根据 sub项目编号 和设备类型 返回储能 电表日充电量、日放电量，单位kWh
   *
   * @param subProjectId sub项目编号
   * @param deviceModel  设备类型
   * @return 通用处理结果
   */
  @GetMapping("/get/day/epeandepi")
  public Result getDayEpeAndEpi(@RequestParam("subProjectId") Long subProjectId,
                                @RequestParam("deviceModel") String deviceModel) {
    List<Long> deviceNos = deviceService.getDeviceNos(subProjectId, deviceModel);
    if (deviceNos != null && !deviceNos.isEmpty()) {
      return Result.getSuccessResultInfo(deviceService.queryFromMongo(deviceNos), "获取电表日充放电量数据成功");
    }
    return Result.getErrorResultInfo("根据sub项目编号 和 设备类型 并未找到对应的 deviceNos (设备编号)");
  }

  /**
   * 根据传入的时间查询 mysql中的并离网时间记录
   *
   * @param date 前端传入的时间
   * @return 通用处理结果
   */
  @GetMapping("/get/mergeleavel/time")
  public Result getMergeLeaveTime(String date, Long subProjectId) {
    List<Triple<Long, Long, Long>> pairs = tOnOffNetworkRecordService.getMergeLeaveTime(date, subProjectId);
    List<Triple<Long, String, String>> triples = pairs.stream().map(o ->
        Triple.of(
            o.getLeft(),
            DateUtils.dateConvert(date, o.getMiddle()),
            DateUtils.dateConvert(date, o.getRight())
        ))
        .collect(Collectors.toList());

    JSONArray jsonArray = new JSONArray();

    triples.forEach(o -> {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("deviceNo", o.getLeft());
      jsonObj.put("leaveNet", o.getMiddle());
      jsonObj.put("mergeNet", o.getRight());
      jsonArray.add(jsonObj);
    });

    return Result.getSuccessResultInfo(jsonArray, "获取并离网时间成功");
  }

  /**
   * 获取并离网时间段的数据
   *
   * @param date 传入的当天的时间  格式 必须是 yyyy-MM-dd
   * @return 通用处理结果
   */
  @GetMapping("/get/mergeleave/data")
  public Result getMergeLeavelDataFromMongo(@RequestParam String date, @RequestParam Long subProjectId) {
    JSONObject result = tOnOffNetworkRecordService.getMergeLeavelDataFromMongo(date, subProjectId);
    return Result.getSuccessResultInfo(result, "获取当天并离网时间段的数据成功");
  }

  /**
   * 重大操作 使用二级密码进行验证
   *
   * @param projectNo 项目编号 : gk
   * @param account   用户名 t_user account字段
   * @param secondPwd 前端传入的 二级密码进行加密后 与数据库中的进行对比
   */
  @GetMapping(value = "/validate/secondpwd")
  public Result validateSecondPwd(@RequestParam(value = "projectNo") String projectNo,
                                  @RequestParam(value = "account") String account,
                                  @RequestParam(value = "secondPwd") String secondPwd) {
    if (StringUtils.isBlank(secondPwd)) {
      return Result.getErrorResultInfo("二级密码不能为空");
    }
    if (secondPwd.length() > 15 || secondPwd.length() < 6) {
      return Result.getErrorResultInfo("二级密码长度应该在 6 -15 位");
    }
    boolean b = userService.validateSecondPwd(projectNo, account, secondPwd);
    if (b) {
      return Result.getSuccessResultInfo();
    } else {
      return Result.getErrorResultInfo("密码错误");
    }
  }

  @PostMapping("/modify/second/pwd")
  public Result modifySecondPwd(@RequestBody UpdatePwdVo vo) {
    try {
      userService.modifyPwd(vo);
    } catch (Exception e) {
      return Result.getErrorResultInfo(e.getMessage());
    }
    return Result.getSuccessResultInfo("二级密码更改成功");
  }

  @PostMapping("/listHistoryByIdTagCode")
  public Result statisticsApi(@RequestBody @Validated StatisticsFormBean form) {
    Pair<Long, Long> pairTime = DateTimeUtils.getCurrentDateHourPair(form.getDate());
    List<Long> deviceNos = form.getDeviceNos();
    List<String> tagcodes = form.getTagcodes();

    List<DeviceRealData> datas = deviceRealDataDao.getTagcodesByNo(deviceNos,pairTime);
    List<List<StatisticsResponseBean> > resultData=new ArrayList<>();

    for (Long itemDeviceNo:deviceNos){
      List<StatisticsResponseBean> res = new ArrayList<>();

      for (String tagcode : tagcodes) {
        StatisticsResponseBean response = new StatisticsResponseBean();
        response.setTagcode(tagcode);
        List<String> times = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (DeviceRealData data : datas) {
          if(itemDeviceNo.longValue()==data.getDeviceNo().longValue()) {
            for (RealMetricsItem item : data.getMetrics()
            ) {
              Long timestamp = item.getTimestamp();
              times.add(String.valueOf(timestamp));
              Object o = item.getDatas().get(tagcode);
              if(o instanceof ArrayList){
                List list=(List<Object>)o;
                vals.add(String.valueOf(list.get(0)));
              }else{
                vals.add(String.valueOf(o));
              }

            }
          }
        }

        response.setTimes(times);
        response.setVals(vals);

        res.add(response);
      }
      resultData.add(res);
    }



    return Result.getSuccessResultInfo(resultData);
  }
}
