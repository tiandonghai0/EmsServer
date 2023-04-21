package com.shmet.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.shmet.api.EHang365Api;
import com.shmet.bean.*;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.*;
import com.shmet.handler.ProjectHandler;
import com.shmet.handler.api.EHang365Handler;
import com.shmet.helper.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.shmet.Consts;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mongodb.StrategyConfigDao;
import com.shmet.dao.mysql.TAcconeDao;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.entity.mysql.gen.TUser;
import com.shmet.handler.AcconeHandler;

@CrossOrigin
@Controller
public class AcconeController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  AcconeHandler acconeHandler;

  @Value("${rabbitmq.queues.updateaccone.request}")
  private String updateAcconeReqQueName;

  @Value("${rabbitmq.exchanges.direct.updateaccone}")
  String updateAcconeExchangeName;

  @Value("${rabbitmq.routingkeys.updateaccone.request}")
  String updateAcconeRequestRoutingKeyName;

  @Value("${filepath.acconefirmware.uploadbasepath}")
  String uploadBasePath;

  @Value("${rabbitmq.exchanges.direct.systemboot}")
  String systemBootExchangeName;

  @Value("${rabbitmq.routingkeys.systemboot.request}")
  String systemBootRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.systemstop}")
  String systemStopExchangeName;

  @Value("${rabbitmq.routingkeys.systemstop.request}")
  String systemStopRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.pcscontrol}")
  String pcscontrolExchangeName;

  @Value("${rabbitmq.routingkeys.pcscontrol.request}")
  String pcscontrolRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.getbatdata}")
  String getbatdataExchangeName;

  @Value("${rabbitmq.routingkeys.getbatdata.request}")
  String getbatdataRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.sendtextcmd}")
  String sendtextcmdExchangeName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.request}")
  String sendtextcmdRoutingKeyName;

  @Value("${rabbitmq.queues.sendtextcmd.response}")
  String sendtextcmdResQueName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.response}")
  String sendtextcmdResponseRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.ehang365eqptstart}")
  String ehang365EqptStartExchangeName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstart.request}")
  String ehang365EqptStartRequestRoutingKeyName;

  @Value("${rabbitmq.exchanges.direct.ehang365eqptstop}")
  String ehang365EqptStopExchangeName;

  @Value("${rabbitmq.routingkeys.ehang365eqptstop.request}")
  String ehang365EqptStopRequestRoutingKeyName;


  @Autowired
  StrategyConfigDao strategyConfigDao;

  @Autowired
  TAcconeDao acconeDao;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  ProjectHandler projectHandler;

  @Autowired
  EHang365Handler hang365Handler;

  /**
   * 登录页面
   */
  @RequestMapping("/accone/updatefirmware/init")
  public ModelAndView acconeUpdateInit() {
    return new ModelAndView("/accone/updatefirmware.btl");
  }

  @ResponseBody
  @RequestMapping(value = "/accone/updatefirmware/datagridload", method = RequestMethod.GET)
  public DataGridBean<AcconeStatusBean> datagridLoadElectric(@RequestParam(required = false) String emuId) {
    List<AcconeStatusBean> statusList = acconeHandler.getAllAcconeStatus();
    if (StringUtils.isNotBlank(emuId)) {
      statusList = statusList.stream().filter(o -> o != null && o.gettAccone() != null)
          .filter(t -> t.gettAccone().getAcconeId() != null &&
              StringUtils.startsWith(String.valueOf(t.gettAccone().getAcconeId()), emuId))
          .collect(Collectors.toList());
    }
    DataGridBean<AcconeStatusBean> db = new DataGridBean<>();
    db.setTotal(statusList.size());
    db.setRows(statusList);
    return db;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/updatefirmware")
  public PostResponseBean updateFirmware(UpdateAcconeBean updateAcconeBean,
                                         HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    if (validateUpdateFirmware(updateAcconeBean)) {
      try {
        List<UpdateAcconeBean> updateAcconeBeanList = new ArrayList<>();
        updateAcconeBean = acconeHandler.updateFirmware(updateAcconeBean);
        updateAcconeBeanList.add(updateAcconeBean);
        this.rabbitTemplate.convertAndSend(updateAcconeExchangeName,
            updateAcconeRequestRoutingKeyName, updateAcconeBeanList, new CorrelationData());
        rst.setSuccessed(true);
      } catch (Exception e) {
        rst.setSuccessed(false);
        rst.setErrorCode(Consts.MSG_CODE_E010000);
        rst.setErrorMsg(e.getMessage());
        logger.error(Consts.MSG_CODE_E010000, e);
      }

    } else {
      rst.setSuccessed(false);
      rst.setErrorCode(Consts.MSG_CODE_E010000);
      rst.setErrorMsg("版本号或AcconeId未设定");
      rst.setSuccessed(false);
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/systemboot/start")
  public PostResponseBean systemBootStart(@RequestBody SystemBootBean systemBootBean,
                                          HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    TAccone accone = acconeDao.findAcconeByAcconeId(systemBootBean.getAcconeId());
    if (accone == null) {
      rst.setErrorMsg("Accone不存在，AcconeID:=" + systemBootBean.getAcconeId());
      rst.setSuccessed(false);
      return rst;
    }
    try {
      StrategyConfig strategyConfig =
          strategyConfigDao.findByAcconeId(systemBootBean.getAcconeId());
      Long strategyNo = 0L;
      if (strategyConfig != null) {
        strategyNo = strategyConfig.getStrategyNo();
      }
      systemBootBean.setSendNo(1);
      systemBootBean.setStatus(0);
      systemBootBean.setStrategyNo(strategyNo);
      String userName = "unknown";
      if (request.getSession().getAttribute(Consts.SESSION_USERINFO) != null) {
        TUser user = (TUser) request.getSession().getAttribute(Consts.SESSION_USERINFO);
        userName = user.getAccount();
      }
      systemBootBean.setUserName(userName);
      List<SystemBootBean> systemBootList = new ArrayList<>();
      systemBootList.add(systemBootBean);
      rabbitTemplate.convertAndSend(systemBootExchangeName, systemBootRequestRoutingKeyName,
          systemBootList, new CorrelationData());
      rst.setSuccessed(true);
    } catch (Throwable t) {
      rst.setSuccessed(false);
      rst.setErrorMsg(t.getMessage());
      t.printStackTrace();
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/systemboot/stop")
  public PostResponseBean systemBootStop(@RequestBody SystemBootBean systemBootBean,
                                         HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    TAccone accone = acconeDao.findAcconeByAcconeId(systemBootBean.getAcconeId());
    if (accone == null) {
      rst.setErrorMsg("Accone不存在，AcconeID:=" + systemBootBean.getAcconeId());
      rst.setSuccessed(false);
      return rst;
    }
    try {
      StrategyConfig strategyConfig =
          strategyConfigDao.findByAcconeId(systemBootBean.getAcconeId());
      Long strategyNo = 0L;
      if (strategyConfig != null) {
        strategyNo = strategyConfig.getStrategyNo();
      }
      systemBootBean.setSendNo(1);
      systemBootBean.setStatus(0);
      systemBootBean.setStrategyNo(strategyNo);
      String userName = "unknown";
      if (request.getSession().getAttribute(Consts.SESSION_USERINFO) != null) {
        TUser user = (TUser) request.getSession().getAttribute(Consts.SESSION_USERINFO);
        userName = user.getAccount();
      }
      systemBootBean.setUserName(userName);
      List<SystemBootBean> systemBootList = new ArrayList<>();
      systemBootList.add(systemBootBean);
      rabbitTemplate.convertAndSend(systemStopExchangeName, systemStopRequestRoutingKeyName,
          systemBootList, new CorrelationData());
      rst.setSuccessed(true);
    } catch (Throwable t) {
      rst.setSuccessed(false);
      rst.setErrorMsg(t.getMessage());
      t.printStackTrace();
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/pcscontrol/process")
  public PostResponseBean pcscontrolProcess(@RequestBody ChargeDisChargeBean chargeDisCahrgeBean,
                                            HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();

    TAccone accone = acconeDao.findAcconeByAcconeId(chargeDisCahrgeBean.getAcconeId());
    if (accone == null) {
      rst.setErrorMsg("Accone不存在，AcconeID:=" + chargeDisCahrgeBean.getAcconeId());
      rst.setSuccessed(false);
      return rst;
    }
    ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(accone.getSubProjectId());
    if (projectConfig == null || projectConfig.getEssAMStatus() == null
        || projectConfig.getEssAMStatus() == 1) {
      rst.setErrorMsg("必须在手动模式下才可设定功率！");
      rst.setSuccessed(false);
      return rst;
    }
    try {
      String userName = "admin";
//      if (request.getSession().getAttribute(Consts.SESSION_USERINFO) != null) {
//        TUser user = (TUser) request.getSession().getAttribute(Consts.SESSION_USERINFO);
//        userName = user.getAccount();
//      }
      chargeDisCahrgeBean.setUserName(userName);
      chargeDisCahrgeBean.setSenderId(2);
      double curChargeSoc = projectConfigDao.getLastSoc(Consts.ELECTRIC_CHARGE, projectConfig);
      double curDisChargeSoc = projectConfigDao.getLastSoc(Consts.ELECTRIC_DISCHARGE, projectConfig);
      if (chargeDisCahrgeBean.getP() > 0) {
        chargeDisCahrgeBean.setChargeType(Consts.ELECTRIC_DISCHARGE);
        chargeDisCahrgeBean.setLastSoc(curDisChargeSoc);
      } else if (chargeDisCahrgeBean.getP() < 0) {
        chargeDisCahrgeBean.setChargeType(Consts.ELECTRIC_CHARGE);
        chargeDisCahrgeBean.setLastSoc(curChargeSoc);
      } else if (chargeDisCahrgeBean.getP() == 0) {
        chargeDisCahrgeBean.setChargeType(Consts.ELECTRIC_STOPPED);
        chargeDisCahrgeBean.setLastSoc(curDisChargeSoc);
      }
      List<ChargeDisChargeBean> chargeDisCahrgeList = new ArrayList<>();
      chargeDisCahrgeList.add(chargeDisCahrgeBean);
      rabbitTemplate.convertAndSend(pcscontrolExchangeName, pcscontrolRequestRoutingKeyName,
          chargeDisCahrgeList, new CorrelationData());
      rst.setSuccessed(true);
    } catch (Throwable t) {
      rst.setSuccessed(false);
      rst.setErrorMsg(t.getMessage());
      t.printStackTrace();
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(value = "/accone/uploadfirmware", method = RequestMethod.POST)
  public PostResponseBean upload(@RequestParam("acconeId") String acconeId,
                                 @RequestParam("ver") String ver, HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    String filePath = uploadBasePath + "/accone/update/firmwares";
    String path = filePath + "/" + acconeId;
    File dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
    req.getFileMap();
    Map<String, MultipartFile> files = req.getFileMap();
    for (String key : files.keySet()) {
      MultipartFile file = files.get(key);
      FileOutputStream fileOutput = null;
      try {
        fileOutput = new FileOutputStream(path + "/update-v" + ver + ".rbl");
        fileOutput.write(file.getBytes());
        fileOutput.flush();
        rst.setSuccessed(true);
      } catch (IOException e) {
        rst.setSuccessed(false);
        rst.setErrorMsg(e.getMessage());
        break;
      } finally {
        if (fileOutput != null) {
          try {
            fileOutput.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(value = "/accone/uploadduf", method = RequestMethod.POST)
  public PostResponseBean uploadduf(@RequestParam("acconeId") String acconeId,
                                    @RequestParam("ver") String ver, HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    String filePath = uploadBasePath + "/accone/update/firmwares";
    String path = filePath + "/" + acconeId;
    File dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
    req.getFileMap();
    Map<String, MultipartFile> files = req.getFileMap();
    for (String key : files.keySet()) {
      MultipartFile file = files.get(key);
      FileOutputStream fileOutput = null;
      try {
        fileOutput = new FileOutputStream(path + "/update-v" + ver + ".duf");
        fileOutput.write(file.getBytes());
        fileOutput.flush();
        rst.setSuccessed(true);
      } catch (IOException e) {
        rst.setSuccessed(false);
        rst.setErrorMsg(e.getMessage());
        break;
      } finally {
        if (fileOutput != null) {
          try {
            fileOutput.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return rst;
  }


  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, value = "/accone/projectconfig/get/{acconeId}")
  public ProjectConfig getProjectConfig(@PathVariable(value = "acconeId") long acconeId) {
    TAccone accone = acconeDao.findAcconeByAcconeId(acconeId);
    if (accone == null) {
      return new ProjectConfig();
    }
    return projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(accone.getSubProjectId());
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/projectconfig/save")
  public PostResponseBean saveProjectConfig(@RequestBody ProjectConfig projectConfig,
                                            HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    Object acconeIdObj = projectConfig.getConfig().get("pcsControlAcconeId");
    if (acconeIdObj == null) {
      rst.setErrorMsg("AcconeId是必需字段，参数缺失AcconeId");
      rst.setSuccessed(false);
      return rst;
    }
    TAccone accone = acconeDao.findAcconeByAcconeId(Long.valueOf((String) acconeIdObj));
    if (accone == null) {
      rst.setErrorMsg("Accone不存在");
      rst.setSuccessed(false);
      return rst;
    }
    ProjectConfig projectConfigToSave = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(accone.getSubProjectId());
    projectConfigToSave.setEssAMStatus(projectConfig.getEssAMStatus());
    projectConfigDao.save(projectConfigToSave);
    rst.setSuccessed(true);
    return rst;
  }

  private boolean validateUpdateFirmware(UpdateAcconeBean updateAcconeBean) {
    if (updateAcconeBean.getAcconeId() == null) {
      return false;
    }
    return updateAcconeBean.getVer() != null && !updateAcconeBean.getVer().equals("");
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/sendtextcmd")
  public Result sendtextcmdProcess(@RequestBody SendTextCmdBean sendTextCmdBean,
                                   HttpServletRequest request, HttpServletResponse response) {
    TAccone accone = acconeDao.findAcconeByAcconeId(sendTextCmdBean.getAcconeId());
    if (accone == null) {

      return Result.getErrorResultInfo("EMUID不存在，EMUID:=" + sendTextCmdBean.getAcconeId());
    }
    try {
      rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
          sendTextCmdBean, new CorrelationData());


//            rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdResponseRoutingKeyName,
//                    sendTextCmdBean, new CorrelationData());
    } catch (Throwable t) {
      t.printStackTrace();
      return Result.getErrorResultInfo(t.getMessage());
    }
    return Result.getSuccessResultInfo("指令下发成功");
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/sendcmd")
  public Result sendtextcmd(@RequestBody JSONObject jsonObject,
                            HttpServletRequest request, HttpServletResponse response) {

    try {
      if (jsonObject.getInteger("command") == null) {
        return Result.getErrorResultInfo("command不能为空");
      }
      if (jsonObject.getInteger("textCmd") == null) {
        return Result.getErrorResultInfo("textCmd不能为空");
      }
      Long emuId = null;
      if (jsonObject.getLong("emuId") == null) {
        ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(jsonObject.getLong("subProjectId"));
        if (projectConfig == null) {
          return Result.getErrorResultInfo("ProjectConfig不存在，subProjectId:=" + jsonObject.get("jsonObject"));
        }
        emuId =  Long.parseLong(projectConfig.getConfig().get("pcsControlAcconeId").toString());
      } else {
        emuId = jsonObject.getLong("emuId");
      }


      SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
      sendTextCmdBean.setAcconeId(emuId);
      sendTextCmdBean.setCommand(jsonObject.getInteger("command"));
      sendTextCmdBean.setTextCmd(jsonObject.getString("textCmd"));
      TAccone accone = acconeDao.findAcconeByAcconeId(sendTextCmdBean.getAcconeId());
      if (accone == null) {

        return Result.getErrorResultInfo("EMUID不存在，EMUID:=" + sendTextCmdBean.getAcconeId());
      }
      rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
          sendTextCmdBean, new CorrelationData());
    } catch (Throwable t) {
      t.printStackTrace();
      return Result.getErrorResultInfo(t.getMessage());
    }
    return Result.getSuccessResultInfo("指令下发成功");
  }

  @PostMapping("/accone/airupdate")
  @ResponseBody
  public Result airControl(@RequestBody ProjectConfig pro) {
    try {
      if (pro == null) {
        return Result.getErrorResultInfo("参数不能为空！");
      }
      ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(pro.getSubProjectId());
      if (projectConfig != null) {
        Boolean fig = projectHandler.updateProjectConfigAirBySubProjectId(pro);
        SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
        sendTextCmdBean.setAcconeId(Long.parseLong(projectConfig.getConfig().get("pcsControlAcconeId").toString()));
        sendTextCmdBean.setCommand(27);
        sendTextCmdBean.setTextCmd(pro.getAir().get("isAuto").toString());

        rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
                sendTextCmdBean, new CorrelationData());
      }

      return Result.getSuccessResultInfo("修改成功");
    } catch (Exception e) {
      return Result.getErrorResultInfo("修改错误：" + e.getMessage());
    }
  }

  @Autowired
  EHang365Api hang365Api;

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/sendhengtong")
  public Result sendHengtong(@RequestBody JSONObject jsonObject,
                             HttpServletRequest request, HttpServletResponse response) {
    Result result = Result.getSuccessResultInfo();
    result.setMessage("执行成功！");
    try {
      if (jsonObject.getInteger("command") == null) {
        return Result.getErrorResultInfo("command不能为空");
      }
      if (jsonObject.getInteger("deviceId") == null) {
        return Result.getErrorResultInfo("deviceId不能为空");
      }
      if (jsonObject.getLong("acconeId") == null) {
        return Result.getErrorResultInfo("acconeId不能为空");
      }

      Integer deviceId = jsonObject.getInteger("deviceId");
      Long acconeId = jsonObject.getLong("acconeId");
      Integer command = jsonObject.getInteger("command");
      JSONObject object = new JSONObject();
      if (command == 13) {
        //用电
        result.setData(hang365Handler.startUseElec(new EHang365StartStopReqBean(), deviceId, acconeId));
      } else if (command == 14) {
        result.setData(hang365Handler.stopUseElec(new EHang365StartStopReqBean(), deviceId, acconeId));
      }
    } catch (Throwable t) {
      hang365Api.setAndLogErrorObject(t, result);

    }
    return result;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/accone/tactic")
  public Result tactic(@RequestBody JSONObject jsonObject,
                       HttpServletRequest request, HttpServletResponse response) {
    Result result = Result.getSuccessResultInfo();
    result.setMessage("执行成功！");
    try {

      Long emuId = null;
      if (jsonObject.getLong("emuId") == null) {
        ProjectConfig projectConfig = projectHandler.getProjectConfigBySubProjectId(jsonObject.getLong("subProjectId"));
        if (projectConfig == null) {
          return Result.getErrorResultInfo("ProjectConfig不存在，subProjectId:=" + jsonObject.get("jsonObject"));
        }
        emuId = (Long) projectConfig.getConfig().get("pcsControlAcconeId");

      } else {
        emuId = jsonObject.getLong("emuId");
      }
      if (emuId == null) {
        result.setSuccess(false);
        result.setMessage("emuId或subProjectId参数不能为空");
        return result;
      }
      Integer command = 24;
      TAccone tAccone = acconeDao.findAcconeByAcconeId(emuId);
      if (tAccone == null) {
        result.setSuccess(false);
        result.setMessage("没有找到EMU设备配置");
        return result;
      }
      ProjectConfig projectConfig = projectConfigDao.findProjectConfigBySubProjectIdNoCache(tAccone.getSubProjectId());
      if (projectConfig == null) {
        result.setSuccess(false);
        result.setMessage("没有找到策略配置");
        return result;
      }
      List<TacticBean> tacticBeanList = new ArrayList<TacticBean>();
      for (ElectricPricePeriod period : projectConfig.getElectricPricePeriod()
      ) {
        TacticBean tacticBean = new TacticBean();
        List<MBean> mBeanList = new ArrayList<MBean>();
        for (SeasonPeriod seasonPeriod : period.getSeasonPeriod()
        ) {
          mBeanList.add(new MBean(seasonPeriod.getMonthFrom(), seasonPeriod.getMonthTo()));
        }
        List<WBean> wBeanList = new ArrayList<WBean>();
        for (ChargePolicy chargePolicy : period.getChargePolicy()
        ) {
          WBean wBean = new WBean();
          wBean.setwS(chargePolicy.getDayOfWeekFrom());
          wBean.setwE(chargePolicy.getDayOfWeekTo());
          List<DBean> dBeanList = new ArrayList<DBean>();
          for (TimePeriod timePeriod : chargePolicy.getPolicy()
          ) {
            DBean dBean = new DBean();
            dBean.setF(timePeriod.getTimeFrom());
            dBean.setT(timePeriod.getTimeTo());
            dBean.setP(timePeriod.getChargeP());
            dBeanList.add(dBean);
          }
          wBean.setD(dBeanList);
          wBeanList.add(wBean);
        }
        tacticBean.setM(mBeanList);
        tacticBean.setW(wBeanList);
        tacticBeanList.add(tacticBean);
      }
      if (tacticBeanList.size() > 0) {
        SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
        sendTextCmdBean.setAcconeId(emuId);
        sendTextCmdBean.setCommand(24);
        sendTextCmdBean.setTextCmd(JsonUtils.objToString(tacticBeanList));
        rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
            sendTextCmdBean, new CorrelationData());
      }

    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage(e.getMessage());
    }
    return result;
  }
}
