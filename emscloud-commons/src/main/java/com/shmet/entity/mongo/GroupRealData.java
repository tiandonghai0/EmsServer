package com.shmet.entity.mongo;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groupRealData")
@CompoundIndexes({@CompoundIndex(name = "group_hour_idx", def = "{'groupNo': 1, 'hour': -1}")})
public class GroupRealData {
    @Id
    private String id;

    @NotNull
    private Long groupNo;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private Integer hour;

    // 并不是所有的组都需要记录实时值
    private List<GroupMetricsItem> metrics;

    // 按小时统计的字段
    private Map<String, Object> statistics;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Long getGroupNo() {
        return groupNo;
    }


    public void setGroupNo(Long groupNo) {
        this.groupNo = groupNo;
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


    public Integer getHour() {
        return hour;
    }


    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public List<GroupMetricsItem> getMetrics() {
        return metrics;
    }


    public void setMetrics(List<GroupMetricsItem> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }


    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }


}
