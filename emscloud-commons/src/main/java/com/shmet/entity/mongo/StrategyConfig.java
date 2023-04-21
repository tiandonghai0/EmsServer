package com.shmet.entity.mongo;

import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 策略配置信息
 */
@Document(collection = "strategyConfig")
public class StrategyConfig {
    @Id
    private String id;

    @NotNull
    private Long strategyNo;

    @NotNull
    private String strategyName;

    private String comment;

    // 项目号
    @NotNull
    private Long projectId;

    // 子项目号
    @NotNull
    private Long subProjectId;

    @NotNull
    private Long acconeId;

    @NotNull
    private int isAuto = 0;

    @NotNull
    private Map<String, Object> params;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Long getStrategyNo() {
        return strategyNo;
    }


    public void setStrategyNo(Long strategyNo) {
        this.strategyNo = strategyNo;
    }

    public Long getProjectId() {
        return projectId;
    }


    public void setProjectId(Long projectId) {
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


    public int getIsAuto() {
        return isAuto;
    }


    public void setIsAuto(int isAuto) {
        this.isAuto = isAuto;
    }


    public Map<String, Object> getParams() {
        return params;
    }


    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
