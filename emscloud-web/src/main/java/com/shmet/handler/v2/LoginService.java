package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.shmet.dao.CustomerMapper;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.ProjectMapper;
import com.shmet.dao.SubProjectMapper;
import com.shmet.entity.mysql.gen.Customer;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.Project;
import com.shmet.entity.mysql.gen.SubProject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LoginService {

  //PCS DeviceModel
  public static final int PCS_DEVICEMODEL = 27;
  //BMS DeviceModel
  public static final int BMS_HEAP_DEVICEMODEL = 28;
  //AIR DeviceModel
  public static final int AIR_DEVICEMODEL = 25;

  @Resource
  private ProjectMapper projectMapper;

  @Resource
  private SubProjectMapper subProjectMapper;

  @Resource
  private DeviceMapper deviceMapper;

  @Resource
  private CustomerMapper customerMapper;

  public Integer getCustomerId(String projectNo) {
    Customer customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("no", projectNo));
    if (customer == null) {
      return -1;
    } else {
      return customer.getCustomerId();
    }
  }

  /**
   * 根据版本查询客户项目
   */
  public List<Customer> getCustomerByVersion(String version) {
    return customerMapper.selectList(new QueryWrapper<Customer>().eq("version", version));
  }

  /**
   * 根据 projectNo 查询客户信息
   *
   * @param projectNo projectNo
   * @return Customer 信息
   */
  public Customer getCustomerByProjectNo(String projectNo) {
    return customerMapper.selectOne(new QueryWrapper<Customer>().eq("no", projectNo));
  }

  /**
   * 根据project_no(customer_no)获取对应的projectIds
   */
  public List<Integer> getProjectIds(String projectNo) {
    Integer customerId = getCustomerId(projectNo);
    return projectMapper.selectList(new QueryWrapper<Project>().eq("customer_id", customerId))
        .stream().map(Project::getProjectId).collect(toList());
  }

  /**
   * 根据projectNo获取对应的所有的 subproject_ids
   */
  public List<Long> getSubProjectIds(String projectNo) {
    List<Integer> projectIds = getProjectIds(projectNo);
    if (projectIds.size() > 0) {
      return subProjectMapper.selectList(new QueryWrapper<SubProject>().in("project_id", projectIds))
          .stream().map(SubProject::getSubProjectId).collect(toList());
    }
    return Lists.newArrayList();
  }

  //根据projectNo 获取 deviceNos
  public List<Long> getDeviceNos(String projectNo) {
    List<Long> subProjectIds = getSubProjectIds(projectNo);
    if (subProjectIds.size() > 0) {
      return deviceMapper.selectList(new QueryWrapper<Device>().in("sub_project_id", subProjectIds)
          .eq("device_model", "1").eq("device_model_type", "1"))
          .stream().map(Device::getDeviceNo).collect(toList());
    }
    return Lists.newArrayList();
  }

  /**
   * 根据subprojectId查询所有的 PCS deviceNos
   *
   * @param subprojectId subprojectId
   * @return deviceNos
   */
  public List<Device> getPcsDevicesBySubprojectId(Long subprojectId) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subprojectId)
        .eq("device_model", PCS_DEVICEMODEL));
  }

  /**
   * 根据subprojectId列表查询所有的 PCS deviceNos
   *
   * @param subprojectId subprojectId
   * @return List<Long>
   */
  public List<Long> getPcsDeviceNosBySubProjectId(Long subprojectId) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subprojectId)
        .eq("device_model", PCS_DEVICEMODEL))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 根据subprojectid 和 deviceModel 查询所有的devices
   *
   * @param subprojectId subprojectId
   * @param deviceModel  deviceModel
   * @return List<Device>
   */
  public List<Device> getDevicesBySubProjectId(Long subprojectId, int deviceModel) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subprojectId)
        .eq("device_model", deviceModel));
  }

  public List<Device> getDevicesBySubProjectIdAndDeviceModelType(Long subprojectId, int deviceModel,int deviceModelType) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", subprojectId)
        .eq("device_model", deviceModel)
        .eq("device_model_type", deviceModelType));
  }

  /**
   * 根据subprojectId列表查询所有的 PCS deviceNos
   *
   * @param subprojectIds subprojectIds
   * @return deviceNos
   */
  public List<Long> getPcsDeviceNosBySubprojectId(List<Long> subprojectIds) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .in("sub_project_id", subprojectIds)
        .eq("device_model", PCS_DEVICEMODEL))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<Long> getDeviceNosBySubprojectIdAndDeviceModel(List<Long> subprojectIds, int deviceModel, int deviceModelType) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .in("sub_project_id", subprojectIds)
        .eq("device_model", deviceModel)
        .eq("device_model_type", deviceModelType))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 根据SubprojectId 查询所有的 电池deviceNos
   *
   * @param subprojectId SubprojectId
   * @return DeviceNos
   */
  public List<Long> getBatteryDeviceNosBySubprojectId(Long subprojectId) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", BMS_HEAP_DEVICEMODEL)
        .eq("sub_project_id", subprojectId))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  public List<Long> getBatteryDeviceNosBySubprojectId(Long subprojectId, int type) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", type)
        .eq("sub_project_id", subprojectId))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 统一获取所有的 电池堆DeviceNos(华银直接从PCS获取)
   *
   * @return DeviceNos
   */
  public List<Long> getBatteryDeviceNos(List<Long> subprojectIds, int type) {
    if (type == BMS_HEAP_DEVICEMODEL) {
      if (subprojectIds != null && subprojectIds.size() > 0) {
        return deviceMapper.selectList(new QueryWrapper<Device>()
            .eq("device_model", BMS_HEAP_DEVICEMODEL)
            .in("sub_project_id", subprojectIds))
            .stream().map(Device::getDeviceNo).collect(toList());
      }
    } else if (type == PCS_DEVICEMODEL) {
      if (subprojectIds != null && subprojectIds.size() > 0) {
        return deviceMapper.selectList(new QueryWrapper<Device>()
            .eq("device_model", PCS_DEVICEMODEL)
            .in("sub_project_id", subprojectIds))
            .stream().map(Device::getDeviceNo).collect(toList());
      }
    }
    return Lists.newArrayList();

  }

  /**
   * 根据subprojectId 获取所有的 deviceNos
   *
   * @param subprojectId subprojectId
   * @return deviceNos
   */
  public List<Long> getAirDeviceNosBySubprojects(Long subprojectId) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", AIR_DEVICEMODEL)
        .eq("sub_project_id", subprojectId))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 统一获取所有的 空调DeviceNos
   *
   * @return DeviceNos
   */
  public List<Long> getAirDeviceNos(List<Long> subprojectIds) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", AIR_DEVICEMODEL)
        .in("sub_project_id", subprojectIds))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 只针对国开 电池组
   *
   * @return deviceNos
   */
  public List<Long> getGkBatteryDeviceNos() {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", "28"))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  /**
   * 只针对保准 电池组
   *
   * @return deviceNos
   */
  public List<Long> getBzBatteryDeviceNos() {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model", "22"))
        .stream().map(Device::getDeviceNo).collect(toList());
  }
}
