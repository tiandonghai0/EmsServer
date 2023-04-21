package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.shmet.bean.AcconeStatusBean;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mongo.AutoExam;
import com.shmet.entity.mongo.AutoExamEntity;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Service
public class AutoExamineService {

  private static final Logger logger = LoggerFactory.getLogger(AutoExamineService.class);

  //***************************************** Constant Key ****************************************
  public static final String REDIS_KEY = "ProjectConfigDao.findElectricProjectConfigBySubProjectId";
  public static final String ACCONE_KEY = "pcsControlAcconeId";
  public static final String ACCONESTATUS_KEY = "TAcconeDao.saveAcconeStatus";
  public static final String EMU_FAILED = "EmuId.Failed";
  public static final String REALDATA_CACHE_KEY = "RealDataRedisDao.saveRealDataCache";
  public static final String ACCONE_MAPPING = "TAcconeDao.findAllUsingAcconeMappedByAcconeId";

  public static final String SIM_API_URL = "http://ec.upiot.net/api/v2/";
  public static final String API_KEY = "d311b86bf0481af64a16b5108dc917b41b6e91ca";
  public static final String SIGN = "?_sign=92100a662b045c4344c1e36b9eba1f89";
  public static final String NEEDAUTOEXAM = "isAutoExam";
  public static final String ALARMCODE_PATH = "/data/emsweb/alarmTagcode.txt";
  //***************************************** Static *********************************************
  public static final Map<String, String> simStatusMap = new ConcurrentHashMap<>();

  static {
    simStatusMap.put("00", "正使用");
    simStatusMap.put("02", "停机");
    simStatusMap.put("03", "预销号");
    simStatusMap.put("04", "销号");
    simStatusMap.put("10", "测试期");
    simStatusMap.put("11", "沉默期");
    simStatusMap.put("12", "停机保号");
    simStatusMap.put("99", "未知");
  }

  //***************************************** Autowired *********************************************
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private CommonService commonService;

  @Resource
  private MongoTemplate mongoTemplate;

  @Resource
  private RestTemplate restTemplate;

  //***************************************** Method *********************************************
  //定时保存
  //@Async
  //@Scheduled(cron = "0 0/20 * * * ?")
  public void examine() {
    logger.info("=========================开始自动巡检 : {} =======================", Thread.currentThread().getName());
    long start = System.currentTimeMillis();
    AutoExam autoExam = new AutoExam();

    List<ProjectNeedInfo> infos = filterInfos();

    //logger.info("filterInfo size: {}", infos.size());
    List<AutoExamEntity> aees = new ArrayList<>();
    //logger.info("info subprojectIds: {}", Joiner.on(",").join(subprojectIds));
    //logger.info("info projectNames: {}", Joiner.on(",").join(names));
    for (ProjectNeedInfo info : infos) {
      AutoExamEntity aee = new AutoExamEntity();
      aee.setSubprojectId(info.getSubProjectId());
      aee.setPrjname(info.getPrjname());
      aee.setEmuStatus(emuStatusIsNormal(info.getAcconeId()) ? 0 : 1);
      aee.setAlarmStatus(alarmStatusIsNormal(info.getSubProjectId()));
      //获取所有 1.0 的组串 deviceNo
      List<Long> deviceNos = getDeviceV1(info.getSubProjectId());
      //获取所有 2.0 的组串 deviceNo
      List<Long> deviceNos2 = getDeviceV2(info.getSubProjectId());
      deviceNos.stream().parallel().forEach(dno -> {
        Object o1 = redisTemplate.opsForHash().get(REALDATA_CACHE_KEY, dno + ".S_STATUS1");
        if (isNormal(o1)) {
          aee.setDigitStatus(1);
          logger.info("异常deviceNo : {}", dno);
        }
      });
      for (Long deviceNo : deviceNos2) {
        Object o2 = redisTemplate.opsForHash().get(REALDATA_CACHE_KEY, deviceNo + ".S_STATUS");
        if (!isNormalV2(o2)) {
          aee.setDigitStatus(1);
          logger.info("异常deviceNo : {}", deviceNo);
          break;
        }
      }
      if (StringUtils.isNotBlank(info.getIccid())) {
        Triple<String, String, String> triple = getSimInfo(info.getIccid());
        aee.setSimStatus(triple.getLeft());
        aee.setSimRemainRate(triple.getMiddle());
        aee.setSimEffectDate(triple.getRight());
      }

      if (numCompare(aee.getEmuStatus()) && numCompare(aee.getDigitStatus())
          && numCompare(aee.getAlarmStatus()) && simStatusAjust(aee.getSimStatus())) {
        aee.setProjectStatus(0);
      } else {
        aee.setProjectStatus(1);
      }

      aees.add(aee);
    }
    autoExam.setTime(Long.parseLong(DateUtils.getCurrentTimeStr(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
    autoExam.setProjectInfos(aees);

    mongoTemplate.save(autoExam);
    logger.info("======================= 自动巡检完毕,耗时: {} ========================", System.currentTimeMillis() - start);
  }

  //查询警告状态信息
  public Integer alarmStatusIsNormal(Long subprojectId) {
    long start = System.currentTimeMillis();
    //logger.info("查询警告状态信息: {}", start);
    //获取deviceNos
    List<Long> deviceNos = getV1V2DeviceNos(subprojectId);
    //获取异常tagcode
    try {
      List<String> tagcodes = Files.readAllLines(Paths.get(ALARMCODE_PATH));
      //logger.info("subprojectId : {} , deviceNos size : {} , tagCodes size: {}", subprojectId, deviceNos.size(), tagcodes.size());
      for (Long dno : deviceNos) {
        for (String tcode : tagcodes) {
          Object o = redisTemplate.opsForHash().get(REALDATA_CACHE_KEY, dno + "." + tcode);
          if (o != null) {
            RealDataItem rd = (RealDataItem) o;
            //logger.info("oooo : {}", ReflectionToStringBuilder.toString(rd, ToStringStyle.MULTI_LINE_STYLE));
            if (rd.getData() instanceof String) {
              if ("1".equals(rd.getData()) || "1.0".equals(rd.getData())) {
                //logger.info("异常点位: {}", dno + "." + tcode);
                return 1;
              }
            }
          }
        }
      }
    } catch (IOException e) {
      logger.error("AutoExamineService IO 异常 : {}", e.getMessage());
    }
    //logger.info("查询警告状态完毕: {}", System.currentTimeMillis() - start);
    return 0;
  }

  public Triple<String, String, String> getSimInfo(String iccid) {
    String path = SIM_API_URL + API_KEY + "/card/" + iccid + SIGN;
    Map<String, Object> map = restTemplate.getForObject(path, Map.class);
    if (map != null && map.get("data") != null
        && StringUtils.equals(String.valueOf(map.get("code")), "200")) {
      Map<String, String> data = (Map<String, String>) map.get("data");
//      logger.info("Sim卡状态: {},Sim卡剩余流量 : {},Sim有效日期: {}",
//          simStatusMap.get(data.get("account_status") == null ? "-1" : data.get("account_status")),
//          data.get("data_balance"),
//          data.get("expiry_date"));
      return Triple.of(simStatusMap.get(data.get("account_status") == null ? "-1" : data.get("account_status")),
          data.get("data_balance"), data.get("expiry_date"));
    }
    return Triple.of("", "", "");
  }

  private boolean simStatusAjust(String simStatus) {
    if (simStatus == null) {
      return true;
    }
    return StringUtils.contains(simStatus, "正使用");
  }

  private boolean numCompare(Integer x) {
    if (x != null) {
      return x == 0;
    }
    return true;
  }

  //emu状态判断
  public Boolean emuStatusIsNormal(Long acconeId) {
    Object o = redisTemplate.opsForHash().get(ACCONESTATUS_KEY, String.valueOf(acconeId));
    if (o != null) {
      AcconeStatusBean bean = (AcconeStatusBean) o;
      return bean.getConnectedStatus() == 1;
    }
    return false;
  }

  @Getter
  @Setter
  public static class IccIdReq {
    private String iccid;
  }

  public JSONObject getNewSimInfo(IccIdReq req) {
    if (req != null && StringUtils.isNotBlank(req.getIccid())) {
      String iccid = req.getIccid();
      JSONObject jsonObj = restTemplate.getForObject(SIM_API_URL + API_KEY + "/card/" + iccid + SIGN, JSONObject.class);
      if (jsonObj != null) {
        if (200 == Integer.parseInt(String.valueOf(jsonObj.get("code")))) {
          jsonObj.put("success", true);
        } else {
          jsonObj.put("success", false);
        }
      }
      return jsonObj;
    } else {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("success", true);
      jsonObj.put("data", null);
      jsonObj.put("code", 200);
      return jsonObj;
    }
  }

  //V1.0 状态判断
  private boolean isNormal(Object o) {
    if (o != null) {
      RealDataItem realDataItem = (RealDataItem) o;
      Object data = realDataItem.getData();
      if (data instanceof String) {
        //正常
        return StringUtils.equals(String.valueOf(data), "0");
      }
      if (data instanceof Long) {
        return ((Long) data) == 0;
      }
    }
    return false;
  }

  //国开 华银 特殊 取二进制最后一位
  private boolean isNormalV2(Object o) {
    if (o != null) {
      RealDataItem realDataItem = (RealDataItem) o;
      Object data = realDataItem.getData();
      if (data instanceof String) {
        long l = Long.parseLong(String.valueOf(data));
        String bstr = Long.toBinaryString(l);
        //正常
        return StringUtils.equals(bstr.substring(bstr.length() - 1), "1");
      }
    }
    return true;
  }

  //过滤组装新的info信息
  private List<ProjectNeedInfo> filterInfos() {
    return redisTemplate.opsForHash()
        .entries(REDIS_KEY).values().stream()
        .filter(o ->
            ((ProjectConfig) o).getProjectId() != null
                && ((ProjectConfig) o).getConfig() != null
                && StringUtils.endsWithIgnoreCase(String.valueOf(((ProjectConfig) o).getConfig().get(NEEDAUTOEXAM)), "1")
        ).map(o -> {
          ProjectConfig config = (ProjectConfig) o;
          ProjectNeedInfo pinfo = new ProjectNeedInfo(config.getProjectId(), config.getSubProjectId(),
              (Long) config.getConfig().get(ACCONE_KEY), config.getComment());
          TAccone tc = (TAccone) redisTemplate.opsForHash().get(ACCONE_MAPPING, String.valueOf(pinfo.getAcconeId()));
          if (tc != null && tc.getIccid() != null) {
            pinfo.setIccid(tc.getIccid());
          }
          return pinfo;
        }).collect(toList());
  }

  //获取组串信息
  public List<Device> getDevices(List<Long> subProjectIds) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .in("sub_project_id", subProjectIds)
            .in("device_model", Lists.newArrayList(23, 29))
    );
  }

  //获取v1.0组串信息
  public List<Long> getDeviceV1(Long subProjectId) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("sub_project_id", subProjectId)
            .eq("device_model", 23)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  //获取v2.0组串信息
  public List<Long> getDeviceV2(Long subProjectId) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("sub_project_id", subProjectId)
            .eq("device_model", 29)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<Long> getV1V2DeviceNos(Long subProjectId) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("sub_project_id", subProjectId)
            .in("device_model", Lists.newArrayList(21, 22, 23, 27, 28, 29))
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  //mongo分页查询
  @Async
  public CompletableFuture<AutoExamResult> query(Integer pageNum, Integer pageSize, Map<String, String> conditionMap) {
    //logger.info("query Result : {}", Thread.currentThread().getName());
    AutoExamResult res = new AutoExamResult(pageNum, pageSize);
    Query query = new Query().with(Sort.by("time").descending()).limit(1);
    AutoExam autoExam = mongoTemplate.findOne(query, AutoExam.class, "autoExam");

    if (autoExam != null) {
      //logger.info("latest time is : {}", autoExam.getTime());
      List<AutoExamEntity> autoExamEntities = (List<AutoExamEntity>) autoExam.getProjectInfos();

      res.setTime(autoExam.getTime());
      //设置 count 属性
      res.setAllCount(Integer.toUnsignedLong(autoExamEntities.size()));
      res.setAllNormalCount(autoExamEntities.stream().filter(o -> Objects.nonNull(o.getProjectStatus()) && 0 == o.getProjectStatus()).count());
      res.setAllNotNormalCount(autoExamEntities.stream().filter(o -> Objects.nonNull(o.getProjectStatus()) && 1 == o.getProjectStatus()).count());
      res.setAlarmExCount(autoExamEntities.stream().filter(o -> Objects.nonNull(o.getAlarmStatus()) && 1 == o.getAlarmStatus()).count());
      res.setDigitExCount(autoExamEntities.stream().filter(o -> Objects.nonNull(o.getDigitStatus()) && 1 == o.getDigitStatus()).count());
      res.setEmuExCount(autoExamEntities.stream().filter(o -> Objects.nonNull(o.getEmuStatus()) && 1 == o.getEmuStatus()).count());
      res.setSimExCount(autoExamEntities.stream().filter(o -> StringUtils.isNotBlank(o.getSimStatus()) && !StringUtils.equals("正使用", o.getSimStatus())).count());

      String isnormal = null, emuStatus = null, digitStatus = null, simStatus = null, alarmStatus = null;
      if (conditionMap != null) {
        isnormal = conditionMap.get("isnormal");
        emuStatus = conditionMap.get("emuStatus");
        digitStatus = conditionMap.get("digitStatus");
        simStatus = conditionMap.get("simStatus");
        alarmStatus = conditionMap.get("alarmStatus");
      }

      if (StringUtils.equals("0", isnormal)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> Objects.nonNull(o.getProjectStatus()) && 0 == o.getProjectStatus()).collect(toList());
        //logger.info("正常项目个数: {}", autoExamEntities.size());
      }
      if (StringUtils.equals("1", isnormal)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> Objects.nonNull(o.getProjectStatus()) && 1 == o.getProjectStatus()).collect(toList());
        //logger.info("异常项目个数: {}", autoExamEntities.size());
      }
      if (StringUtils.equals("1", emuStatus)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> Objects.nonNull(o.getEmuStatus()) && 1 == o.getEmuStatus()).collect(toList());
        //logger.info("emu异常个数: {}", autoExamEntities.size());
      }
      if (StringUtils.equals("1", digitStatus)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> Objects.nonNull(o.getDigitStatus()) && 1 == o.getDigitStatus()).collect(toList());
        //logger.info("组串异常个数: {}", autoExamEntities.size());
      }
      if (StringUtils.equals("1", simStatus)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> StringUtils.isNotBlank(o.getSimStatus()) && !StringUtils.equals("正使用", o.getSimStatus())).collect(toList());
        //logger.info("Sim异常个数: {}", autoExamEntities.size());
      }
      if (StringUtils.isNotBlank(alarmStatus) && StringUtils.equals("1", alarmStatus)) {
        autoExamEntities = autoExamEntities.stream().filter(o -> Objects.nonNull(o.getAlarmStatus()) && 1 == o.getAlarmStatus()).collect(toList());
        //logger.info("警告异常个数: {}", autoExamEntities.size());
      }

      List<AutoExamEntity> limitExamEntities = autoExamEntities.stream().skip((long) (pageNum - 1) * pageSize).limit(pageSize).collect(toList());

      res.setPageCount(autoExamEntities.size() % pageSize + 1);
      res.setTotalCount(autoExamEntities.size());
      res.setSuccess(true);
      res.setData(limitExamEntities);
      res.setCode("200");
      res.setMessage("查询成功");
    }
    return CompletableFuture.completedFuture(res);
  }

  public static class AutoExamResult extends ResultSearch {
    private Long allCount;
    private Long allNormalCount;
    private Long allNotNormalCount;
    private Long digitExCount;
    private Long emuExCount;
    private Long simExCount;
    private Long alarmExCount;
    private Long time;

    public AutoExamResult() {
    }

    public AutoExamResult(int pageNo, int pageSize) {
      super(pageNo, pageSize);
    }

    public Long getAllCount() {
      return allCount;
    }

    public void setAllCount(Long allCount) {
      this.allCount = allCount;
    }

    public Long getAllNormalCount() {
      return allNormalCount;
    }

    public void setAllNormalCount(Long allNormalCount) {
      this.allNormalCount = allNormalCount;
    }

    public Long getAllNotNormalCount() {
      return allNotNormalCount;
    }

    public void setAllNotNormalCount(Long allNotNormalCount) {
      this.allNotNormalCount = allNotNormalCount;
    }

    public Long getDigitExCount() {
      return digitExCount;
    }

    public void setDigitExCount(Long digitExCount) {
      this.digitExCount = digitExCount;
    }

    public Long getEmuExCount() {
      return emuExCount;
    }

    public void setEmuExCount(Long emuExCount) {
      this.emuExCount = emuExCount;
    }

    public Long getSimExCount() {
      return simExCount;
    }

    public void setSimExCount(Long simExCount) {
      this.simExCount = simExCount;
    }

    public Long getAlarmExCount() {
      return alarmExCount;
    }

    public void setAlarmExCount(Long alarmExCount) {
      this.alarmExCount = alarmExCount;
    }

    public Long getTime() {
      return time;
    }

    public void setTime(Long time) {
      this.time = time;
    }

    @Override
    public String toString() {
      return "AutoExamResult{" +
          "digitExCount=" + digitExCount +
          ", emuExCount=" + emuExCount +
          ", simExCount=" + simExCount +
          ", alarmExCount=" + alarmExCount +
          '}';
    }
  }

  private List<AutoExamEntity> filterSimEle(List<AutoExamEntity> autoExamEntities, String status) {
    return autoExamEntities.stream()
        .filter(o ->
            StringUtils.isNotBlank(o.getSimStatus())
                &&
                !StringUtils.equals("正使用", String.valueOf(o.getSimStatus()))
        )
        .collect(toList());
  }

  static class ProjectNeedInfo {
    private Integer projectId;
    private Long subProjectId;
    private Long acconeId;
    private String iccid;
    private String prjname;

    public ProjectNeedInfo() {
    }

    public ProjectNeedInfo(Integer projectId, Long subProjectId, Long acconeId) {
      this.projectId = projectId;
      this.subProjectId = subProjectId;
      this.acconeId = acconeId;
    }

    public ProjectNeedInfo(Integer projectId, Long subProjectId, Long acconeId, String prjname) {
      this.projectId = projectId;
      this.subProjectId = subProjectId;
      this.acconeId = acconeId;
      this.prjname = prjname;
    }

    public Integer getProjectId() {
      return projectId;
    }

    public void setProjectId(Integer projectId) {
      this.projectId = projectId;
    }

    public Long getSubProjectId() {
      return subProjectId;
    }

    public void setSubProjectId(Long subProjectId) {
      this.subProjectId = subProjectId;
    }

    public Long getAcconeId() {
      return acconeId;
    }

    public void setAcconeId(Long acconeId) {
      this.acconeId = acconeId;
    }

    public String getPrjname() {
      return prjname;
    }

    public String getIccid() {
      return iccid;
    }

    public void setIccid(String iccid) {
      this.iccid = iccid;
    }

    public void setPrjname(String prjname) {
      this.prjname = prjname;
    }

    @Override
    public String toString() {
      return "ProjectNeedInfo{" +
          "projectId=" + projectId +
          ", subProjectId=" + subProjectId +
          ", acconeId=" + acconeId +
          ", iccid='" + iccid + '\'' +
          ", prjname='" + prjname + '\'' +
          '}';
    }
  }
}
