package com.shmet.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.Consts;
import com.shmet.aop.OperationLogAnnotation;
import com.shmet.bean.ChargeDisChargeBean;
import com.shmet.bean.SendTextCmdBean;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mysql.TAcconeDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.field.AirConditionerPeriod;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.handler.ProjectHandler;
import com.shmet.helper.ExcelUtils;
import com.shmet.helper.GuidUuidUtils;
import com.shmet.helper.JsonUtils;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Controller
public class ProjectController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  ProjectHandler projectHandler;

  @Autowired
  TAcconeDao acconeDao;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Value("${rabbitmq.exchanges.direct.pcscontrol}")
  String pcscontrolExchangeName;

  @Value("${rabbitmq.routingkeys.pcscontrol.request}")
  String pcscontrolRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.getbatdata}")
  String getbatdataExchangeName;

  @Value("${rabbitmq.exchanges.onOffNetwork.request}")
  String onOffNetworkExchangeName;

  @Value("${rabbitmq.routingkeys.onOffNetwork.request}")
  String onOffNetworkRoutingKeyName;

  @Value("${rabbitmq.queues.onOffNetwork.request}")
  String onOffNetworkName;

  @Value("${rabbitmq.exchanges.direct.sendtextcmd}")
  String sendtextcmdExchangeName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.request}")
  String sendtextcmdRoutingKeyName;

  /*空调控制*/
  @Value("${rabbitmq.exchanges.air.request}")
  String airExchangeName;
  @Value("${rabbitmq.routingkeys.air.request}")
  String airRoutingKeyName;
  @Value("${rabbitmq.queues.air.request}")
  String airName;
  /*空调控制*/

  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * 根据subProjectId获取ProjectConfig
   *
   * @param jsonObject json对象
   * @return String
   */
  @RequestMapping("/project/getProjectConfig")
  @ResponseBody
  public String getProjectConfig(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();

    try {
      Object obj = jsonObject.get("subProjectId");
      if (obj == null) {
        result.setMessage("参数subProjectId不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      Long subPorjectId = Long.parseLong(obj.toString());
      ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(subPorjectId);
      if (projectConfig != null) {
        result.setData(projectConfig);
      }
    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }


  /**
   * 修改 ProjectConfig
   *
   * @param jsonObject json对象
   * @return String
   */
  @OperationLogAnnotation(logType = 3, opContent = "更新储能策略")
  @PostMapping("/project/updateProjectConfig")
  @ResponseBody
  public String updateProjectConfig(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();

    try {
      if (jsonObject == null) {
        result.setMessage("参数不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      ProjectConfig projectConfig = JsonUtils.stringToObj(jsonObject.toString(), ProjectConfig.class);
      if (projectConfig != null) {
        boolean fig = projectHandler.updateProjectConfigBySubProjectId(projectConfig);
        result.setData(fig);
      }

    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 修改 ProjectConfig>config
   *
   * @param jsonObject jsonObject
   */
  @OperationLogAnnotation(logType = 3, opContent = "更新储能策略")
  @PostMapping("/project/updateProjectConfigConfig")
  @ResponseBody
  public String updateProjectConfigConfig(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();

    try {
      if (jsonObject == null) {
        result.setMessage("参数不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      ProjectConfig projectConfig = JsonUtils.stringToObj(jsonObject.toString(), ProjectConfig.class);
      if (projectConfig != null) {
        Boolean fig = projectHandler.updateProjectConfigConfigBySubProjectId(projectConfig);
        result.setData(fig);
      }

    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 修改空调策略
   *
   * @param jsonObject json对象
   * @return String
   */
  @OperationLogAnnotation(logType = 3, opContent = "更新空调策略")
  @RequestMapping("/project/updateProjectConfigAir")
  @ResponseBody
  public String updateProjectConfigAir(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();

    try {
      if (jsonObject == null) {
        result.setMessage("参数不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      ProjectConfig projectConfig = JsonUtils.stringToObj(jsonObject.toString(), ProjectConfig.class);
      if (projectConfig != null) {
        boolean fig = projectHandler.updateProjectConfigAirConditionerPeriodBySubProjectId(projectConfig);
        result.setData(fig);
      }

    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  @OperationLogAnnotation(logType = 6, opContent = "修改EMU手自动状态")
  @ResponseBody
  @PostMapping("/v2/project/updateEMU")
  public Result updateEmus(@RequestBody EmuVo emuVo) {
    Map<Long, Integer> map = new LinkedHashMap<>();
    if (Objects.nonNull(emuVo) && CollectionUtils.isNotEmpty(emuVo.getSubprojectIds())) {
      List<ProjectConfig> configs = projectConfigDao.findElectricProjectConfigBySubProjectIdsNoCache(emuVo.getSubprojectIds());
      if (configs != null && configs.size() > 0) {
        configs.forEach(e -> {
          e.setEssAMStatus(emuVo.getEssAMStatus());
          projectConfigDao.save(e);
          //下发指令给工控机，修改策略状态
          SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
          sendTextCmdBean.setAcconeId(Long.parseLong(e.getConfig().get("pcsControlAcconeId").toString()));
          sendTextCmdBean.setCommand(27);
          sendTextCmdBean.setTextCmd(emuVo.getEssAMStatus().toString());

          rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
                  sendTextCmdBean, new CorrelationData());
        });
        map = configs.stream().collect(toMap(ProjectConfig::getSubProjectId, ProjectConfig::getEssAMStatus, (o1, o2) -> o2));
      }
      return Result.getSuccessResultInfo(map);
    } else {
      return Result.getErrorResultInfo("传入参数不能为空");
    }
  }

  @ToString
  private static class EmuVo {
    private List<Long> subprojectIds;
    private Integer essAMStatus;

    public List<Long> getSubprojectIds() {
      return subprojectIds;
    }

    public void setSubprojectId(List<Long> subprojectIds) {
      this.subprojectIds = subprojectIds;
    }

    public Integer getEssAMStatus() {
      return essAMStatus;
    }

    public void setEssAMStatus(Integer essAMStatus) {
      this.essAMStatus = essAMStatus;
    }
  }

  /**
   * 更新EMU
   *
   * @param projectConfig projectConfig
   * @return String
   */
  @OperationLogAnnotation(logType = 6, opContent = "修改EMU手自动状态")
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/project/updateEMU")
  public String updateEMU(@RequestBody ProjectConfig projectConfig) {
    Result result = Result.getSuccessResultInfo();
    Long subProjectId = projectConfig.getSubProjectId();
    Object acconeIdObj = projectConfig.getConfig().get("pcsControlAcconeId");
    if (projectConfig.getEssAMStatus() == null || projectConfig.getEssAMStatus().toString().equals("")) {
      result.setMessage("essAMStatus是必需字段，参数缺失essAMStatus");
      result.setSuccess(false);
      return JsonUtils.objToString(result);
    }

    ProjectConfig projectConfigToSave = null;

    if (subProjectId != null) {
      projectConfigToSave = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(Long.parseLong(subProjectId.toString()));

    } else if (acconeIdObj != null && !acconeIdObj.toString().equals("")) {

      TAccone accone = acconeDao.findAcconeByAcconeId(Long.valueOf((String) acconeIdObj));
      if (accone == null) {
        result.setMessage("Accone不存在");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      projectConfigToSave = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(accone.getSubProjectId());

    }
    if (projectConfigToSave == null) {
      result.setMessage("subProjectId或者pcsControlAcconeId没有找到策略");
      result.setSuccess(false);
      return JsonUtils.objToString(result);
    }
    projectConfigToSave.setEssAMStatus(projectConfig.getEssAMStatus());
    projectConfigDao.save(projectConfigToSave);
    //下发指令给工控机，修改策略状态
    SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
    sendTextCmdBean.setAcconeId(Long.parseLong(projectConfig.getConfig().get("pcsControlAcconeId").toString()));
    sendTextCmdBean.setCommand(27);
    sendTextCmdBean.setTextCmd(projectConfig.getEssAMStatus().toString());

    rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
            sendTextCmdBean, new CorrelationData());
    return JsonUtils.objToString(result);
  }

  /**
   * 生成Excel
   *
   * @param jsonObject json对象
   * @return String
   */
  @RequestMapping("/project/createExcel")
  @ResponseBody
  public String createExcel(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();

    try {
      if (jsonObject == null) {

        result.setMessage("参数不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      String fileName = jsonObject.getString("fileName");
      if (fileName == null || fileName.length() == 0) {
        fileName = GuidUuidUtils.getGuidorUuid();
      }

      String titile = jsonObject.getString("title");
      if (titile.isEmpty()) {
        result.setMessage("参数titile不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      String sheetName = jsonObject.getString("sheetName");

      String type = jsonObject.getString("type");
      if (type.isEmpty()) {
        result.setMessage("参数type不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      String unit = jsonObject.getString("unit");
      if (unit.isEmpty()) {
        result.setMessage("参数unit不能为空！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      int fontSize = jsonObject.getInteger("fontSize");
      if (fontSize < 12) {
        result.setMessage("参数fontSize不能小于12！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      int width = jsonObject.getInteger("width");
      if (width < 300) {
        result.setMessage("参数width不能小于300！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      int height = jsonObject.getInteger("height");
      if (height < 300) {
        result.setMessage("参数height不能小于300！");
        result.setSuccess(false);
        return JsonUtils.objToString(result);
      }

      int reportType = jsonObject.getInteger("reportType");

      JSONObject dataObj = jsonObject.getJSONObject("datas");
      String dataString = JsonUtils.objToString(dataObj);

      //Map<String, List<Map<String, Double>>> datas = JsonUtils.stringToObj(dataString, Map.class);
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, List<Map<String, Double>>> datas = objectMapper.readValue(dataString, new TypeReference<Map<String, List<Map<String, Double>>>>() {
      });

      fileName = fileName + ".xls";
      String filePath = "";

      String url = ExcelUtils.createExcel(datas, sheetName, titile, type, unit, fontSize, width, height, filePath, fileName, reportType);
      result.setData(url);

    } catch (Exception e) {
      logger.error("Exception:" + e.getMessage(), e);
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 充放电停机控制
   *
   * @param jsonObject json对象
   * @return String
   */
  @OperationLogAnnotation(logType = 6, opContent = "充放电停机控制")
  @ResponseBody
  @PostMapping(value = "/project/pcscontrol")
  public String pcsControl(@RequestBody JSONObject jsonObject) {
    Result result = Result.getSuccessResultInfo();
    Long subProjectId = jsonObject.getLong("subProjectId");
    double p = jsonObject.getDouble("p");
    Integer chargeType = jsonObject.getInteger("chargeType");
    Integer esNo = jsonObject.getInteger("esNo");

    if (subProjectId == null) {
      result.setSuccess(false);
      result.setMessage("subProjectId项目id不能为空");
      return JsonUtils.objToString(result);
    }
    if (chargeType == 0) {
      result.setSuccess(false);
      result.setMessage("chargeType充电类型错误");
      return JsonUtils.objToString(result);
    }
    if (p < 0) {
      result.setSuccess(false);
      result.setMessage("p功率不能为负数");
      return JsonUtils.objToString(result);
    }
//    if (esNo == 0) {
//      result.setSuccess(false);
//      result.setMessage("esNo不能为空或0");
//      return JsonUtils.objToString(result);
//    }


    ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(subProjectId);
    if (projectConfig == null || projectConfig.getEssAMStatus() == null || projectConfig.getEssAMStatus() == 1) {

      result.setSuccess(false);
      result.setMessage("必须在手动模式下才可设定功率！");
      return JsonUtils.objToString(result);
    }
    try {
      ChargeDisChargeBean chargeDisCahrgeBean = new ChargeDisChargeBean();
      chargeDisCahrgeBean.setAcconeId(Long.parseLong(projectConfig.getConfig().get("pcsControlAcconeId").toString()));
      chargeDisCahrgeBean.setP(p);
      chargeDisCahrgeBean.setChargeType(chargeType);


      String userName = "baishu";
      chargeDisCahrgeBean.setUserName(userName);
      chargeDisCahrgeBean.setSenderId(2);
      double curChargeSoc = projectConfigDao.getLastSoc(Consts.ELECTRIC_CHARGE, projectConfig);
      double curDisChargeSoc = projectConfigDao.getLastSoc(Consts.ELECTRIC_DISCHARGE, projectConfig);

      if (chargeType == Consts.ELECTRIC_DISCHARGE) {
        chargeDisCahrgeBean.setP(p);
        chargeDisCahrgeBean.setLastSoc(curDisChargeSoc);
        chargeDisCahrgeBean.setEsNo(esNo);
      } else if (chargeType == Consts.ELECTRIC_CHARGE) {
        chargeDisCahrgeBean.setP(0 - p);
        chargeDisCahrgeBean.setEsNo(esNo);
        chargeDisCahrgeBean.setLastSoc(curChargeSoc);
      } else if (chargeType == Consts.ELECTRIC_STOPPED) {
        chargeDisCahrgeBean.setP(0.0);
        chargeDisCahrgeBean.setLastSoc(curDisChargeSoc);
        chargeDisCahrgeBean.setEsNo(esNo);
      }

      List<ChargeDisChargeBean> chargeDisCahrgeList = new ArrayList<>();
      chargeDisCahrgeList.add(chargeDisCahrgeBean);
      rabbitTemplate.convertAndSend(pcscontrolExchangeName, pcscontrolRequestRoutingKeyName,
          chargeDisCahrgeList, new CorrelationData());

    } catch (Throwable t) {
      result.setSuccess(false);
      result.setMessage(t.getMessage());
      t.printStackTrace();
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 更新并离网 状态 下发命令 至 RabbitMQ
   * 1. onOffNetworkStatus==1 时 点击 检修停机 按钮 更新 onOffNetworkStatus==0 在将消息 传至MQ
   * 2. onOffNetworkStatus==0 时 点击 开启紧急供电 按钮 更新 onOffNetworkStatus==1 在将消息 传至MQ
   *
   * @param subProjectId subProjectId
   */
  @PostMapping("/project/downstatus")
  public Result changeStatus(@RequestParam Long subProjectId, @RequestParam int status) {
    ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(subProjectId);
    if (status != 0 && status != 1) {
      return Result.getErrorResultInfo("status 必须是0 或 1");
    }
    if (projectConfig == null) {
      return Result.getErrorResultInfo("没有找到subProjectId 对应的projectConfig");
    }
    JSONObject object = new JSONObject();
    object.put("emuId", projectConfig.getConfig().get("pcsControlAcconeId"));
    projectHandler.updateMergeLeaveStatus(projectConfig, status);
    object.put("onOffNetworkStatus", 0);
    rabbitTemplate.convertAndSend(onOffNetworkExchangeName, onOffNetworkRoutingKeyName, object);
    return Result.getSuccessResultInfo("指令下发成功");
  }

  /**
   * 更新并离网 状态 下发命令 至 RabbitMQ
   * esNo代表空调编号,1就是1号，2就是2号，因为多台空调；
   * t代表温度，0表示关机，负数表示制冷，正数表示制热
   *
   * @param jsonObject emuId  emu编号 esNo 空调编号 t 温度，0表示关机，负数表示制冷，正数表示制热
   */
  @OperationLogAnnotation(logType = 6, opContent = "风机控制")
  @PostMapping("/project/aircontrol")
  @ResponseBody
  public Result airControl(@RequestBody JSONObject jsonObject) {

    try {
      if (jsonObject.getInteger("emuId") == null) {
        return Result.getErrorResultInfo("emuId不能为空");
      }
      if (jsonObject.getInteger("esNo") == null) {
        return Result.getErrorResultInfo("esNo不能为空");
      }
      if (jsonObject.getInteger("t") == null) {
        return Result.getErrorResultInfo("t不能为空");
      }
      if (jsonObject.getLong("subProjectId") != null) {

        if (jsonObject.getInteger("t") != 0) {
          //不是关键，制冷或制热，需要修改mongodb参数
          ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(jsonObject.getLong("subProjectId"));
          if (projectConfig != null) {
            AirConditionerPeriod airConditionerPeriod = projectConfig.getAirConditionerPeriod();
            if (jsonObject.getInteger("t") > 0) {
              airConditionerPeriod.setHot(jsonObject.getInteger("t"));
            } else {
              airConditionerPeriod.setCool(jsonObject.getInteger("t"));
            }
            boolean fig = projectHandler.updateProjectConfigAirConditionerPeriodBySubProjectId(projectConfig);
            if (!fig) {
              return Result.getErrorResultInfo("设置保存失败！");
            }
          }
        }
      }
      rabbitTemplate.convertAndSend(airName, airRoutingKeyName, jsonObject);
      return Result.getSuccessResultInfo("指令下发成功");
    } catch (Exception e) {
      return Result.getErrorResultInfo("空调控制下发错误：" + e.getMessage());
    }
  }

  @PostMapping("/project/getaircontrol")
  @ResponseBody
  public Result getAirControl(@RequestBody JSONObject jsonObject) {
    if (jsonObject.getLong("subProjectId") == null) {
      return Result.getErrorResultInfo("subProjectId不能为空");
    }
    ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(jsonObject.getLong("subProjectId"));
    if (projectConfig == null) {
      return Result.getErrorResultInfo("根据subProjectId没有找到数据");
    }

    return Result.getSuccessResultInfo(projectConfig.getAirConditionerPeriod());

  }

}
