package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.dao.SubProjectMapper;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.SubProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
public class SubProjectService {

  @Resource
  private SubProjectMapper subProjectMapper;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  DeviceService deviceService;

  /**
   * 根据subprojectId 获取电池总容量
   *
   * @param subprojectId subprojectId
   * @return 电池总容量
   */
  public Double getTotalCapacity(Long subprojectId) {
    return subProjectMapper.selectOne(
        new QueryWrapper<SubProject>().eq("sub_project_id", subprojectId)
    ).getTotalCapacity();
  }

  /**
   * 根据subprojectIds 获取电池总容量
   *
   * @param subprojectIds subprojectIds
   * @return 电池总容量
   */
  public List<Double> getTotalCapacitys(List<Long> subprojectIds) {
    return subProjectMapper.selectList(
        new QueryWrapper<SubProject>().in("sub_project_id", subprojectIds)
    ).stream().map(SubProject::getTotalCapacity).collect(Collectors.toList());
  }

  /**
   *根据projectName模糊查询项目信息
   * @param projectName
   * @return
   */
  public List<SubProject> getV2Project(String projectName){
    return subProjectMapper.getV2Project(projectName);

  }
  /**
   *根据projectName模糊查询项目信息
   * @param projectName
   * param sysType  系统类型0:储能,1:岸电
   * @return
   */
  public List<Map<String,Object>> getV2Project(String projectName,String sysType){
    return subProjectMapper.getV2ProjectInfo(projectName,sysType);

  }

  public Map<String,Object> getV2ProjectDetail(Long subProjectId){
    ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(subProjectId);
    Map<String,Object> map =new HashMap<>();
    map.put("emsRunStatus",projectConfig.getEssAMStatus());
    map.put("pcsId",deviceService.getDeviceIds(subProjectId,"27"));
    map.put("bmsId",deviceService.getDeviceIds(subProjectId,"28"));
    map.put("sbmuId",deviceService.getDeviceIds(subProjectId,"29"));
    map.put("subProjectId",subProjectId);
    map.put("projectId",projectConfig.getProjectId());
    map.put("subProjectName",projectConfig.getComment());
    return map;
  }
}
