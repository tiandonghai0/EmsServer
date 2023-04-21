package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 特殊定义的充放电策略
 */
@Document(collection = "chargePolicyCustom")
public class ChargePolicyCustom {

    @Id
    private String id;

    // 项目号
    @NotNull
    private Integer projectId;

    // 子项目号
    @NotNull
    private Long subProjectId;

    private Integer dayFrom;
    private Integer dayTo;
    private List<TimePeriod> policy;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the subProjectId
     */
    public Long getSubProjectId() {
        return subProjectId;
    }

    /**
     * @param subProjectId the subProjectId to set
     */
    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    /**
     * @return the dayFrom
     */
    public Integer getDayFrom() {
        return dayFrom;
    }

    /**
     * @param dayFrom the dayFrom to set
     */
    public void setDayFrom(Integer dayFrom) {
        this.dayFrom = dayFrom;
    }

    /**
     * @return the dayTo
     */
    public Integer getDayTo() {
        return dayTo;
    }

    /**
     * @param dayTo the dayTo to set
     */
    public void setDayTo(Integer dayTo) {
        this.dayTo = dayTo;
    }

    /**
     * @return the policy
     */
    public List<TimePeriod> getPolicy() {
        return policy;
    }

    /**
     * @param policy the policy to set
     */
    public void setPolicy(List<TimePeriod> policy) {
        this.policy = policy;
    }

}
