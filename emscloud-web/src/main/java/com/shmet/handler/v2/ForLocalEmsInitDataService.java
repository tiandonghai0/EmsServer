package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shmet.DateTimeUtils;
import com.shmet.controller.v2.ForLocalEmsInitDataController;
import com.shmet.dao.*;
import com.shmet.entity.mongo.DeviceNoTagCodeItem;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ForLocalEmsInitDataService {

  private final CustomerMapper customerMapper;
  private final ProjectMapper projectMapper;
  private final AcconeMapper acconeMapper;
  private final DeviceService deviceService;
  private final UserMapper userMapper;
  private final UserRoleRelMapper userRoleRelMapper;
  private final SubProjectMapper subProjectMapper;
  private final DeviceMapper deviceMapper;
  private final MongoTemplate mongoTemplate;

  public static final int CN_DID = 1;
  public static final int SHID_DID = 10;
  public static final int GF_DID = 20;
  public static final int CDZ_DID = 30;
  public static final int HJKZQ_DID = 42;
  public static final int BMS_DID = 41;
  public static final int GFNBQ_DID = 50;
  public static final int FD_DID = 60;
  public static final int SHUID_DID = 70;
  public static final int HD_DID = 80;
  public static final int PCS_DID = 43;
  public static final int BYQ_DID = 90;
  public static final int ZC_DID = 200;
  public static final int DX_DID = 10000;

  //public static final int INIT_PROJECTCONFIG = 10300034;

  private static final Map<Integer, String> map = new HashMap<>(16);
  private static final Set<String> calcTagcodeSet = new HashSet<>(2);

  @Value("${filepath.logo.default}")
  private String defaultLogo;

  static {
    map.put(1, "储能电表");
    map.put(2, "市电电表");
    map.put(3, "光伏电表");
    map.put(4, "充电桩电表");
    map.put(5, "风电电表");
    map.put(6, "水电电表");
    map.put(7, "火电电表");
    map.put(27, "PCS");
    map.put(28, "BMS");
    map.put(29, "组串");
    map.put(30, "电芯");
    map.put(46, "环境控制器");
    map.put(51, "变压器");
    map.put(44, "光伏逆变器");
    calcTagcodeSet.add("EPE");
    calcTagcodeSet.add("EPI");
  }

//  private ProjectConfig getInitProjectConfig() {
//    return mongoTemplate.findOne(
//        Query.query(Criteria.where("projectId").is(INIT_PROJECTCONFIG)), ProjectConfig.class, "projectConfig"
//    );
//  }

  private ProjectConfig getInitProjectConfigBySubprojectId(Long subprojectId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("subProjectId").is(subprojectId)), ProjectConfig.class, "projectConfig"
    );
  }

  public Integer getMaxCustomerId() {
    return customerMapper.selectList(null)
        .stream().map(Customer::getCustomerId).max(Comparator.comparingInt(i -> i)).orElse(-1);
  }


  public void saveCustomer(int nextInsertCustomerId, Customer customer, ForLocalEmsInitDataController.CloudInitDataReq req) {
    String customerNo = req.getCustomerNo();
    if (existCustomerNo(customerNo)) {
      throw new RuntimeException("客户编号已存在,请重新输入");
    } else {
      customer.setCustomerId(nextInsertCustomerId);
      customer.setCustomerName(req.getCustomerName());
      customer.setCustomerNo(customerNo);
      customer.setVersion("EMS2.0");
      customer.setAdminAccount(customerNo);
      customer.setEmail(req.getEmail());
      customer.setAddr(req.getAddr());
      customer.setContactName(req.getContact());
      customer.setMobile(req.getMobile());
      customer.setCity(req.getCityCode());
      customer.setCreateDate(new Date());
      customer.setLogo(defaultLogo);
      customerMapper.insert(customer);
    }
  }

  public void saveProject(int intProjectId, int nextInsertCustomerId, Project project, ForLocalEmsInitDataController.CloudInitDataReq req) {
    project.setProjectId(intProjectId);
    project.setProjectName(req.getProjectName());
    project.setCustomerId(nextInsertCustomerId);
    project.setAddr(req.getAddr());
    projectMapper.insert(project);
  }

  public void saveSubProject(int intProjectId, SubProject subProject, ForLocalEmsInitDataController.CloudInitDataReq req) {
    subProject.setSubProjectId(req.getProjectId());
    subProject.setProjectId(intProjectId);
    subProject.setSubProjectName(req.getProjectName());
    subProject.setTotalCapacity(req.getTotalCapacity());
    subProject.setBatteryType(req.getBatteryType());

    subProjectMapper.insert(subProject);
  }

  public void saveAccone(Long projectId) {
    if (!existAcconeId(projectId)) {
      Accone accone = new Accone();
      accone.setAcconeId(projectId);
      accone.setSubProjectId(projectId);
      accone.setAcconeSn(String.valueOf(projectId));
      accone.setVer("1.0");
      accone.setStatus(1);
      accone.setRunningStatus(1);
      acconeMapper.insert(accone);
    } else {
      throw new RuntimeException("acconeId [ " + projectId + " ] 已经存在数据库,无法进行保存");
    }
  }

  public boolean existProjectId(Integer projectId) {
    return projectMapper.selectOne(
        Wrappers.<Project>lambdaQuery().eq(Project::getProjectId, projectId)) != null;
  }

  public boolean existSubProjectId(Long subprojectId) {
    return subProjectMapper.selectOne(
        Wrappers.<SubProject>lambdaQuery().eq(SubProject::getSubProjectId, subprojectId)) != null;
  }

  public boolean existSubProjectIdInDevice(Long subprojectId) {
    return deviceMapper.selectOne(
        Wrappers.<Device>lambdaQuery().eq(Device::getSubProjectId, subprojectId)) != null;
  }

  public boolean existAcconeId(Long acconeId) {
    return acconeMapper.selectOne(
        Wrappers.<Accone>lambdaQuery().eq(Accone::getAcconeId, acconeId)) != null;
  }

  public void saveDevice(Long subprojectId, int deviceType, String deviceModel, Integer deviceNum) {
    if (!existSubProjectIdInDevice(subprojectId) && deviceNum != null) {
      List<Device> devices = new ArrayList<>(deviceNum);
      for (int i = 0; i < deviceNum; i++) {
        Device device = new Device();
        int deviceId = deviceType + i;
        device.setDeviceId(deviceId);
        device.setDeviceNo(subprojectId * 100000 + deviceId);
        device.setSubProjectId(subprojectId);
        if (Integer.parseUnsignedInt(deviceModel) <= 7) {
          device.setDeviceModel("1");
          switch (deviceModel) {
            case "1":
              device.setDeviceModelType(1);
              break;
            case "2":
              device.setDeviceModelType(0);
              break;
            case "3":
              device.setDeviceModelType(2);
              break;
            case "5":
              device.setDeviceModelType(3);
              break;
            case "4":
              device.setDeviceModelType(4);
              break;
          }
        } else {
          device.setDeviceModel(deviceModel);
        }
        device.setDeviceName(map.get(Integer.parseUnsignedInt(deviceModel)) + (i + 1));
        device.setStatus(1);
        device.setAcconeId(subprojectId);

        devices.add(device);
      }

      deviceService.saveBatch(devices, 2000);
    } else {
      throw new RuntimeException("subprojectId [ " + subprojectId + " ] 已经存在数据库,无法进行保存");
    }

  }

  public void saveDxDevice(Long subprojectId, int deviceType, String deviceModel, Integer deviceNum, List<Object> dxfzNum) {
    if (!existSubProjectIdInDevice(subprojectId) && deviceNum != null) {
      List<Device> devices = new ArrayList<>(deviceNum);
      int k = 0, m;
      for (int i = 0; i < dxfzNum.size(); i++) {
        m =Integer.parseInt( dxfzNum.get(i).toString());
        for (int j = k; j < k + m; j++) {
          Device device = new Device();
          int deviceId = deviceType + j;
          device.setDeviceId(deviceId);
          device.setDeviceNo(subprojectId * 100000 + deviceId);
          device.setSubProjectId(subprojectId);
          device.setDeviceModel(deviceModel);
          device.setDeviceName(map.get(Integer.parseUnsignedInt(deviceModel)) + (j + 1));
          device.setStatus(1);
          device.setAcconeId(subprojectId);
          device.setParentDeviceNo(Long.parseLong(String.valueOf((ZC_DID + i))));

          devices.add(device);
        }
        k += m;
      }

      deviceService.saveBatch(devices, 2000);
    } else {
      throw new RuntimeException("subprojectId [ " + subprojectId + " ] 已经存在数据库,无法进行保存");
    }

  }

  private boolean existCustomerNo(String customerNo) {
    return customerMapper.selectOne(
        Wrappers.<Customer>lambdaQuery().eq(Customer::getCustomerNo, customerNo)
    ) != null ||
        userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getProjectNo, customerNo)) != null;
  }

  public void saveUser(String customerNo) {
    User user = new User();
    String pwd = "Jz123456&";
    String email = customerNo + "@gmail.com";
    user.setAccount(customerNo);
    user.setPassword(pwd);
    user.setEmail(email);
    user.setProjectNo(customerNo);
    userMapper.insert(user);
  }

  public void saveUserRoleRel(String userId) {
    UserRoleRel userRoleRel = new UserRoleRel();
    userRoleRel.setId(String.valueOf(RandomUtils.nextInt(1000, 10000)));
    userRoleRel.setUserId(userId);
    userRoleRel.setRoleId("5");

    userRoleRelMapper.insert(userRoleRel);
  }

  @Transactional(rollbackFor = Exception.class)
  public void initCloudData(ForLocalEmsInitDataController.CloudInitDataReq req) {
    long begin = System.currentTimeMillis();
    Long projectId = req.getProjectId();
    int intProjectId = projectId.intValue();
    Integer maxCustomerId = getMaxCustomerId();
    int nextInsertCustomerId = maxCustomerId + 1;

    if (maxCustomerId != -1) {
      saveCustomer(nextInsertCustomerId, new Customer(), req);
    }
    if (!existProjectId(intProjectId)) {
      saveProject(intProjectId, nextInsertCustomerId, new Project(), req);
    } else {
      throw new RuntimeException("projectId [ " + projectId + " ] 已经存在数据库,无法进行保存");
    }
    if (!existSubProjectId(projectId)) {
      saveSubProject(intProjectId, new SubProject(), req);
    } else {
      throw new RuntimeException("subprojectId [ " + projectId + " ] 已经存在数据库,无法进行保存");
    }
    saveAccone(projectId);
    saveDevice(projectId, CN_DID, "1", req.getCnNum());
    saveDevice(projectId, SHID_DID, "2", req.getShidNum());
    saveDevice(projectId, GF_DID, "3", req.getGfNum());
    saveDevice(projectId, CDZ_DID, "4", req.getCdzNum());
    saveDevice(projectId, FD_DID, "5", req.getFdNum());
    saveDevice(projectId, SHUID_DID, "6", req.getShuidNum());
    saveDevice(projectId, HD_DID, "7", req.getHdNum());
    saveDevice(projectId, PCS_DID, "27", req.getPcsNum());
    saveDevice(projectId, BYQ_DID, "51", req.getByqNum());
    saveDevice(projectId, ZC_DID, "29", req.getZcNum());
    if(req.getDxfzNum()==null||req.getDxfzNum().size()==0 || req.getDxfzNum().size()!=req.getZcNum()){
      List<Object> integerList=new ArrayList<>();
      Integer zc=req.getDxNum()/req.getZcNum();
      for (int i=0;i<req.getZcNum();i++){
        integerList.add(zc);
      }
      req.setDxfzNum(integerList);
    }
    if (
        (req.getZcNum() != req.getDxfzNum().size())
            ||
            (req.getDxfzNum().stream().mapToInt(d -> Integer.parseInt(d.toString()) ).sum() != req.getDxNum())
    ) {
      throw new RuntimeException("组串数量与电芯分组数量不匹配 或者 电芯分组数量之和跟电芯数量不一致");
    }
    saveDxDevice(projectId, DX_DID, "30", req.getDxNum(), req.getDxfzNum());
    saveDevice(projectId, HJKZQ_DID, "46", req.getHjkzqNum());
    saveDevice(projectId, BMS_DID, "28", req.getBmsNum());
    saveDevice(projectId, GFNBQ_DID, "44", req.getGfnbqNum());

    saveUser(req.getCustomerNo());
    saveUserRoleRel(req.getCustomerNo());

    //ProjectConfig initProjectConfig = getInitProjectConfig();
    ProjectConfig initProjectConfig = getInitProjectConfigBySubprojectId(req.getTemplateId());
    if (initProjectConfig != null && !existProjectConfig(projectId)) {
      ProjectConfig newProjectConfig = new ProjectConfig();
      newProjectConfig.setProjectId(intProjectId);
      newProjectConfig.setSubProjectId(projectId);
      newProjectConfig.setElectricPricePeriod(initProjectConfig.getElectricPricePeriod());
      newProjectConfig.setStrategySaveTime(DateTimeUtils.getCurrentLongTime());
      newProjectConfig.setEssAMStatus(initProjectConfig.getEssAMStatus());
      newProjectConfig.setEssRunStatus(initProjectConfig.getEssRunStatus());
      newProjectConfig.setComment(projectId + "策略");
      Map<String, Object> config = initProjectConfig.getConfig();
      config.put("pcsControlAcconeId", projectId);
      config.put("isTemplate", false);
      Integer batteryType = req.getBatteryType();
      if (batteryType == 0) {
        config.put("maxV", 3.50);
        config.put("minV", 2.95);
      }
      if (batteryType == 2) {
        config.put("maxV", 2.4);
        config.put("minV", 1.8);
      }
      newProjectConfig.setConfig(config);
      List<DeviceNoTagCodeItem> initDeviceNoTagCodeListToCalcPrice = initProjectConfig.getDeviceNoTagCodeListToCalcPrice();
      if (initDeviceNoTagCodeListToCalcPrice != null) {
        newProjectConfig.setDeviceNoTagCodeListToCalcPrice(initDeviceNoTagCodeListToCalcPrice);
      } else {
        List<DeviceNoTagCodeItem> calcItems = new ArrayList<>(req.getCnNum());
        for (int i = 0; i < req.getCnNum(); i++) {
          Long cnDeviceNo = projectId * 100000 + CN_DID + i;
          for (String tcode : calcTagcodeSet) {
            DeviceNoTagCodeItem item = new DeviceNoTagCodeItem();
            item.setDeviceNo(cnDeviceNo);
            item.setTagCode(tcode);
            calcItems.add(item);
          }
        }
        newProjectConfig.setDeviceNoTagCodeListToCalcPrice(calcItems);
      }
      mongoTemplate.insert(newProjectConfig);
    } else {
      throw new RuntimeException("模板策略 [" + req.getTemplateId() + " ]不存在或者 subProjectId [" + projectId + " ]的策略已经存在,请重新输入projectId");
    }

    long end = System.currentTimeMillis();
    log.info("总耗时:{}", (end - begin) / 1000.0);
  }

  private boolean existProjectConfig(Long subprojectId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("subProjectId").is(subprojectId)),
        ProjectConfig.class,
        "projectConfig"
    ) != null;
  }
}
