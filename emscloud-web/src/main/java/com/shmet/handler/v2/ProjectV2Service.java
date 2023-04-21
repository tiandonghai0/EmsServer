package com.shmet.handler.v2;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.shmet.dao.ProjectMapper;
import com.shmet.entity.mongo.DeviceRealData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toSet;

/**
 * @author
 */
@Service
@RequiredArgsConstructor
public class ProjectV2Service {

  private static final Map<Integer, ProjectReqDto> mergeMap = new ConcurrentHashMap<>();

  private final MongoTemplate mongoTemplate;

  @Resource
  private ProjectMapper projectMapper;

  public DeviceRealData query(Long emuId, Integer deviceId, Integer hour) {
    Query query = Query.query(
        Criteria.where("hour").is(hour)
            .and("deviceId").is(deviceId)
            .and("acconeId").is(emuId)
    );
    query.fields().exclude("statistics");
    return mongoTemplate.findOne(
        query,
        DeviceRealData.class,
        "deviceRealData"
    );
  }

  static {
    mergeMap.put(8, new ProjectReqDto("国开储能", Lists.newArrayList(20201001, 20201002)));
  }

  //获取所有的 project_id project_name 同一项目 下的 project_id 要合并
  public Set<ProjectResVo> queryMergeProject() {
    return projectMapper.selectList(null)
        .stream().map(o -> {
          if (o.getCustomerId() != null && mergeMap.get(o.getCustomerId()) != null) {
            return new ProjectResVo(
                Joiner.on(",").join(mergeMap.get(o.getCustomerId()).getProjectIds()),
                mergeMap.get(o.getCustomerId()).getProjectName()
            );
          } else {
            return new ProjectResVo(
                String.valueOf(o.getProjectId()),
                o.getProjectName()
            );
          }
        }).collect(toSet());
  }

  static class ProjectReqDto {
    private String projectName;
    private List<Integer> projectIds;

    public ProjectReqDto(String projectName, List<Integer> projectIds) {
      this.projectName = projectName;
      this.projectIds = projectIds;
    }

    public String getProjectName() {
      return projectName;
    }

    public void setProjectName(String projectName) {
      this.projectName = projectName;
    }

    public List<Integer> getProjectIds() {
      return projectIds;
    }

    public void setProjectIds(List<Integer> projectIds) {
      this.projectIds = projectIds;
    }
  }

  static class ProjectResVo {
    private String projectId;
    private String projectName;

    public ProjectResVo(String projectId, String projectName) {
      this.projectId = projectId;
      this.projectName = projectName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ProjectResVo that = (ProjectResVo) o;
      return Objects.equal(projectId, that.projectId) && Objects.equal(projectName, that.projectName);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(projectId, projectName);
    }

    public String getProjectId() {
      return projectId;
    }

    public void setProjectId(String projectId) {
      this.projectId = projectId;
    }

    public String getProjectName() {
      return projectName;
    }

    public void setProjectName(String projectName) {
      this.projectName = projectName;
    }
  }
}
