package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.shmet.aop.AccessLimit;
import com.shmet.aop.OperationLogAnnotation;
import com.shmet.aop.UserLoginToken;
import com.shmet.bean.SendTextCmdBean;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.handler.v2.ProjectV2Service;
import com.shmet.handler.v2.SubProjectService;
import com.shmet.helper.JsonUtils;
import com.shmet.utils.DateUtils;
import com.shmet.utils.DynamicExcelUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Slf4j
@RestController
@RequestMapping("/v2")
public class ProjectV2Controller {

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  private ProjectV2Service projectV2Service;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private SubProjectService subProjectService;

  @Value("${rabbitmq.exchanges.direct.sendtextcmd}")
  String sendtextcmdExchangeName;

  @Value("${rabbitmq.routingkeys.sendtextcmd.request}")
  String sendtextcmdRoutingKeyName;


  /**
   * 储能控制
   *
   * @param jsonObject json对象   页面下发指令 { projectId:123456, data:[{ t:1,通用指令下发t tcode:"AAA",通用指令下发tcoded id:41,通用指令下发did m:30通用指令下发m  modeType}]}
   * @return String
   */
  @OperationLogAnnotation(logType = 6, opContent = "储能控制")
  @ResponseBody
  @PostMapping(value = "/project/control")
  @UserLoginToken
  public Result control(@RequestBody JSONObject jsonObject) {
    Result result=Result.getSuccessResultInfo();
    Long subProjectId=jsonObject.getLong("subProjectId");
    ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(subProjectId);
    //if (projectConfig == null || projectConfig.getEssAMStatus() == null || projectConfig.getEssAMStatus() == 1) {
    if (projectConfig == null || projectConfig.getEssAMStatus() == null) {
      result.setSuccess(false);
      result.setMessage("策略不存在！");
      return result;
    }
    SendTextCmdBean sendTextCmdBean = new SendTextCmdBean();
    sendTextCmdBean.setAcconeId(Long.parseLong(projectConfig.getConfig().get("pcsControlAcconeId").toString()));
    sendTextCmdBean.setCommand(29);
    sendTextCmdBean.setTextCmd(JsonUtils.objToString(jsonObject.get("data")));
    System.out.println("储能控制:"+JsonUtils.objToString(sendTextCmdBean));
    rabbitTemplate.convertAndSend(sendtextcmdExchangeName, sendtextcmdRoutingKeyName,
            sendTextCmdBean, new CorrelationData());
    result.setMessage("指令下发成功！");
    return result;
  }

  /**
   * 获取v2储能项目信息
   * @param jsonObject
   * @return
   */
  @PostMapping("project/getprojectv2")
  @UserLoginToken
  public Result getV2Project(@RequestBody JSONObject jsonObject){
    String projectId=null;
    String sysType=null;
    try {
      if (jsonObject.containsKey("projectName")) {
        projectId = jsonObject.getString("projectName");
      }
      if (jsonObject.containsKey("sysType")) {
        sysType = jsonObject.getString("sysType");
      }
      return Result.getSuccessResultInfo(subProjectService.getV2Project(projectId,sysType));
    }catch (Exception ex){
      return Result.getErrorResultInfo(ex.getMessage());
    }
  }

  /**
   * 获取v2储能项目详细信息
   * @param jsonObject
   * @return
   */
  @PostMapping("project/getprojectdetailv2")
  @UserLoginToken
  public Result getV2ProjectDetail(@RequestBody JSONObject jsonObject){
    try {
      if (!jsonObject.containsKey("subProjectId")) {
       return Result.getErrorResultInfo("subProjectId不能为空！");
      }
      return Result.getSuccessResultInfo(subProjectService.getV2ProjectDetail(jsonObject.getLong("subProjectId")));
    }catch (Exception ex){
      return Result.getErrorResultInfo(ex.getMessage());
    }
  }

  @PostMapping("/project/strategy")
  @UserLoginToken
  public Result queryStrategy(@RequestBody Map<String, String> param) {
    String projectName = param.get("projectName");
    Query query = Query.query(
        Criteria.where("comment").exists(true)
            .and("projectId").nin(20200009)
    );
    if (StringUtils.isNotBlank(projectName)) {
      query.addCriteria(Criteria.where("comment").regex(projectName));
    }
    List<ProjectConfig> projects = mongoTemplate.find(
        query,
        ProjectConfig.class,
        "projectConfig"
    );
    List<ProjectStrategy> strategies = convertToStrategy(projects);
    log.info("查询到的策略数量: " + strategies.size());
    return Result.getSuccessResultInfo(strategies);
  }

  @GetMapping("/project/merge_query")
  @UserLoginToken
  public Result queryMergeProject() {
    return Result.getSuccessResultInfo(projectV2Service.queryMergeProject());
  }

  @PostMapping("/backend/query")
  @UserLoginToken
  public Result query(@RequestBody @Validated BackendReq req) {
    if (req != null) {
      return Result.getSuccessResultInfo(projectV2Service.query(req.getEmuId(), req.getDeviceId(), req.getHour()));
    } else {
      return Result.getErrorResultInfo("缺少必传参数[emuId 或 deviceId 或 hour]");
    }
  }

  @AccessLimit(seconds = 5, maxCounts = 10)
  @RequestMapping("/downExcel")
  @UserLoginToken
  public void downloadFailedUsingJson(@RequestBody(required = false) @Validated BackendReq req, HttpServletResponse response) throws IOException {
    final DeviceRealData realData = projectV2Service.query(req.getEmuId(), req.getDeviceId(), req.getHour());
    //final DeviceRealData realData = projectV2Service.query(867605051287728L, 3, 2021070615);
    try {
      //数据
      List<Map<String, Object>> dataList = new ArrayList<>();
      //头
      LinkedHashMap<String, String> head = Maps.newLinkedHashMap();
      head.put("time", "time");
      for (RealMetricsItem metric : realData.getMetrics()) {
        final Long timestamp = metric.getTimestamp();
        final Map<String, Object> datas = metric.getDatas();
        for (Map.Entry<String, Object> entry : datas.entrySet()) {
          head.put(entry.getKey(), entry.getKey());
        }
        final Map<String, Object> obj = metric.getDatas();
        obj.put("time", DateUtils.formatterStrTime(String.valueOf(timestamp)));
        dataList.add(obj);
      }
      byte[] stream = DynamicExcelUtils.exportExcelFile(head, dataList);
      response.setContentType("application/vnd.ms-excel");
      response.setCharacterEncoding("utf-8");
      // 这里URLEncoder.encode可以防止中文乱码 当然和easy excel没有关系
      String fileName = URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8").replaceAll("\\+", "%20");
      response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
      ServletOutputStream out = response.getOutputStream();
      out.write(stream);
      out.close();
    } catch (Exception e) {
      response.reset();
      response.setContentType("application/json");
      response.setCharacterEncoding("utf-8");
      Result result = Result.getErrorResultInfo("下载文件失败" + e.getMessage());
      response.getWriter().println(objectMapper.writeValueAsString(result));
    }
  }

  private List<ProjectStrategy> convertToStrategy(List<ProjectConfig> configs) {
    return configs.stream().map(o -> {
      ProjectStrategy strategy = new ProjectStrategy();
      strategy.setProjectId(o.getProjectId());
      strategy.setSubProjectId(o.getSubProjectId());
      strategy.setComment(o.getComment());
      return strategy;
    }).collect(toList());
  }

  @Getter
  @Setter
  static class BackendReq {
    @NotNull(message = "emuId 不能为空")
    @Max(value = 9223372036854775807L, message = "emuId传入不合法")
    private Long emuId;
    @NotNull(message = "deviceId 不能为空")
    @Max(value = 1000, message = "deviceId传入不合法")
    @Min(value = 1, message = "deviceId传入不合法")
    private Integer deviceId;
    @NotNull(message = "hour 不能为空")
    @Min(value = 2020000100, message = "hour传入的值不合法")
    @Max(value = 2030000100, message = "hour传入的值不合法")
    private Integer hour;
  }

  @Getter
  @Setter
  static class ProjectStrategy {
    private String comment;
    //项目号
    private Integer projectId;
    //子项目号
    private Long subProjectId;
  }
}
