package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scriptConfig")
public class ScriptConfig {
    @Id
    private String id;

    @NotNull
    private Long scriptId;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private Integer gatewayId;

    @NotNull
    String ver;

    static public String getCollectionName() {
        return "scriptConfig";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Long getScriptId() {
        return scriptId;
    }


    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
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


    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }


    public String getVer() {
        return ver;
    }


    public void setVer(String ver) {
        this.ver = ver;
    }

}
