package com.shmet.entity.mongo;

import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dummyDeviceConfig")
public class DummyDeviceConfig {
    @Id
    private String id;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private Long deviceNo;

    @NotNull
    private String realDataImpl;

    @NotNull
    private Map<String, Object> config;

    @NotNull
    private String comment;

    static public String getCollectionName() {
        return "dummyDeviceConfig";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getDeviceNo() {
        return deviceNo;
    }


    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }


    public String getRealDataImpl() {
        return realDataImpl;
    }


    public void setRealDataImpl(String realDataImpl) {
        this.realDataImpl = realDataImpl;
    }


    public Map<String, Object> getConfig() {
        return config;
    }


    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

}
