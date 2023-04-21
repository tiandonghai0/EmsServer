package com.shmet.entity.mongo;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author
 */
@Document(collection = "autoExam")
@CompoundIndexes({
    @CompoundIndex(name = "time_idx", def = "{'time': -1}")
})
public class AutoExam {
  private Long time;
  private List<AutoExamEntity> projectInfos;

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public List<AutoExamEntity> getProjectInfos() {
    return projectInfos;
  }

  public void setProjectInfos(List<AutoExamEntity> projectInfos) {
    this.projectInfos = projectInfos;
  }
}
