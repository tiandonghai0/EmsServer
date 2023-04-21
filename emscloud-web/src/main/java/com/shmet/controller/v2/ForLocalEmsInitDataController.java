package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.shmet.aop.UserLoginToken;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.SubProjectMapper;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.DeviceTagConfig;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.SubProject;
import com.shmet.handler.v2.ForLocalEmsInitDataService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
public class ForLocalEmsInitDataController {

  private final MongoTemplate mongoTemplate;
  private final DeviceMapper deviceMapper;
  private final SubProjectMapper subProjectMapper;
  private final ForLocalEmsInitDataService forLocalEmsInitDataService;

  //1:电表 27:PCS 28:电堆 29:电池组 30:电芯 44:光伏逆变器 45:储能 46:环境控制器
  private static final List<String> deviceModels = Lists.newArrayList("1", "27", "28", "29", "30", "44", "45", "46");

  @RequestMapping("/projectconfig")
  @UserLoginToken
  public ProjectConfig getProjectConfigBySubprojectId(@RequestBody JSONObject jsonObject) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("subProjectId").is(jsonObject.get("subProjectId"))),
        ProjectConfig.class,
        "projectConfig"
    );
  }

  @RequestMapping("/tagcode")
  public List<TagcodeVo> getTagcodes() {
    return mongoTemplate.find(
        Query.query(Criteria.where("deviceModel").in(deviceModels)),
        DeviceTagConfig.class,
        "deviceTagConfig"
    ).stream()
        .map(
            d -> new TagcodeVo(
                d.getTagCode(), d.getTagName(), Integer.parseUnsignedInt(d.getDeviceModel())
            )
        )
        .collect(toList());
  }

  @RequestMapping("/device")
  @UserLoginToken
  public List<DeviceVo> getDeviceBySubprojectId(@RequestBody JSONObject jsonObject) {
    return deviceMapper.selectList(
        Wrappers.<Device>lambdaQuery().eq(Device::getSubProjectId, jsonObject.get("subProjectId"))
    ).stream().map(d -> {
          Long parentDeviceNo = d.getParentDeviceNo();

          Integer pno = null;
          if (parentDeviceNo != null) {
            pno = parentDeviceNo.intValue();
          }
          return new DeviceVo(
              d.getDeviceId(),
              d.getDeviceName(),
              Integer.parseUnsignedInt(d.getDeviceModel()),
              pno);
        }
    ).collect(toList());
  }

  @RequestMapping("/subproject")
  public List<SubprojectVo> getSubProjectBySubprojectId(@RequestBody JSONObject jsonObject) {
    return subProjectMapper.selectList(
        Wrappers.<SubProject>lambdaQuery().eq(SubProject::getSubProjectId, jsonObject.get("subProjectId"))
    ).stream()
        .map(s -> new SubprojectVo(
                s.getSubProjectId(),
                s.getProjectId().longValue(),
                s.getSubProjectName(),
                s.getTotalCapacity(),
                s.getBatteryType()
            )
        ).collect(toList());
  }

  @PostMapping("/template-list")
  public Result getTemplateList() {
    return Result.getSuccessResultInfo(mongoTemplate.find(
        Query.query(Criteria.where("config.isTemplate").is(true)),
        ProjectConfig.class,
        "projectConfig"
    ).stream().map(p -> {
      TemplateVo tvo = new TemplateVo();
      tvo.setSubProjectId(p.getSubProjectId());
      tvo.setComment(p.getComment());
      return tvo;
    }).collect(toList()));

  }


  @PostMapping("/cloud")
  public Result initCloudData(@RequestBody @Validated ForLocalEmsInitDataController.CloudInitDataReq req) {
    try {
      forLocalEmsInitDataService.initCloudData(req);
    } catch (Exception e) {
      e.printStackTrace();
      return Result.getErrorResultInfo("初始化失败: " + e.getMessage());
    }
    return Result.getSuccessResultInfo("初始化云端数据成功");
  }

  //prohectId=subprojectId=acconeId
  @Getter
  @Setter
  @NoArgsConstructor
  public static class CloudInitDataReq {
    @Max(value = 99999999, message = "projectId不能大于99999999")
    private Long projectId;
    //电池容量
    @NotNull(message = "电池容量不能为空")
    private Double totalCapacity;
    //项目名称
    @NotNull(message = "项目名称不能为空")
    private String projectName;
    //客户名称
    @NotNull(message = "客户名称不能为空")
    private String customerName;
    //联系人
    @NotNull(message = "联系人不能为空")
    private String contact;
    //联系电话
    @NotNull(message = "联系电话不能为空")
    private String mobile;
    //cityCode
    @NotNull(message = "城市编码不能为空")
    private String cityCode;
    //邮箱
    @NotNull(message = "邮箱不能为空")
    private String email;
    //地址
    @NotNull(message = "地址不能为空")
    private String addr;
    //客户编号
    @NotNull(message = "客户编号不能为空")
    private String customerNo;
    //储能电表数量
    @NotNull(message = "储能电表数量不能为空,若没有,请用0表示")
    private Integer cnNum;
    //市电电表数量
    @NotNull(message = "市电电表数量不能为空,若没有,请用0表示")
    private Integer shidNum;
    //光伏电表数量
    @NotNull(message = "光伏电表数量不能为空,若没有,请用0表示")
    private Integer gfNum;
    //充电桩电表数量
    @NotNull(message = "充电桩电表数量不能为空,若没有,请用0表示")
    private Integer cdzNum;
    //风电电表数量
    @NotNull(message = "风电桩电表数量不能为空,若没有,请用0表示")
    private Integer fdNum;
    //水电电表数量
    @NotNull(message = "水电桩电表数量不能为空,若没有,请用0表示")
    private Integer shuidNum;
    //火电电表数量
    @NotNull(message = "火电桩电表数量不能为空,若没有,请用0表示")
    private Integer hdNum;
    //pcs数量
    @NotNull(message = "pcs数量不能为空,若没有,请用0表示")
    private Integer pcsNum;
    //变压器数量
    @NotNull(message = "变压器电表数量不能为空,若没有,请用0表示")
    private Integer byqNum;
    //组串数量
    @NotNull(message = "组串数量不能为空,若没有,请用0表示")
    private Integer zcNum;
    //电芯数量
    @NotNull(message = "电芯数量不能为空,若没有,请用0表示")
    private Integer dxNum;
    //环境控制器 0 或 1
    @NotNull(message = "环境控制器电表数量不能为空,若没有,请用0表示")
    private Integer hjkzqNum;
    //光伏逆变器
    @NotNull(message = "光伏逆变器数量不能为空,若没有,请用0表示")
    private Integer gfnbqNum;
    //BMS数量
    @NotNull(message = "BMS数量不能为空,若没有,请用0表示")
    private Integer bmsNum;
    //电芯分组数量
    @NotNull(message = "电芯分组数量不能为空")
    private List<Object> dxfzNum;
    @Max(value = 99999999, message = "模板策略Id不能大于99999999")
    private Long templateId;
    //0 锂电池 2 铅炭电池
    private Integer batteryType;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class TemplateVo {
    private Long subProjectId;
    private String comment;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class DeviceVo {
    private Integer deviceId;
    private String deviceName;
    private Integer modelType;
    private Integer parentId;

    public DeviceVo(Integer deviceId, String deviceName, Integer modelType, Integer parentId) {
      this.deviceId = deviceId;
      this.deviceName = deviceName;
      this.modelType = modelType;
      this.parentId = parentId;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class SubprojectVo {
    private Long subprojectId;
    private Long projectId;
    private String subprojectName;
    //额定容量
    private Double totalCapacity;
    private Integer batteryType;

    public SubprojectVo(Long subprojectId, Long projectId, String subprojectName, Double totalCapacity, Integer batteryType) {
      this.subprojectId = subprojectId;
      this.projectId = projectId;
      this.subprojectName = subprojectName;
      this.totalCapacity = totalCapacity;
      this.batteryType = batteryType;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  private static class TagcodeVo {
    private String tagcode;
    private String tagcodeName;
    private Integer modelType;

    public TagcodeVo(String tagcode, String tagcodeName, Integer modelType) {
      this.tagcode = tagcode;
      this.tagcodeName = tagcodeName;
      this.modelType = modelType;
    }
  }
}
