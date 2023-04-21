package com.shmet.entity.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shmet.entity.mongo.field.AirConditionerPeriod;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 项目配置信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "projectConfig")
public class ProjectConfig {

    @Id
    private String id;

    private String comment;

    //策略保存时间 yyyy-MM-dd HH:mm:ss
    private Long strategySaveTime;

    // 项目号
    @NotNull
    private Integer projectId;

    @NotNull
    private Integer essRunStatus;

    @NotNull
    private Integer essAMStatus;

    // 子项目号
    @NotNull
    private Long subProjectId;

    @NotNull
    private Map<String, Object> config;

    @NotNull
    private List<ElectricPricePeriod> electricPricePeriod;

    @NotNull
    private DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeStatus;

    @NotNull
    private List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckSoc;

    @NotNull
    private List<DeviceNoTagCodeItem> deviceNoTagCodeListToCalcPrice;

    /**
     * 最高电压
     */
    private DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeMaxV;

    /**
     * 最低压力
     */
    private DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeMinV;

    /**
     * 最高温度
     */
    private List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckMaxT;

    private Map<String,Object> air;

    @NotNull
    // deviceNoTagCodeToCheckChargeStatus的点位值，正放，负充，则设定为1
//deviceNoTagCodeToCheckChargeStatus的点位值，正充，负放，则设定为-1
    private int checkFactor;
    private int powerFactor;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public Long getStrategySaveTime() {
        return strategySaveTime;
    }

    public void setStrategySaveTime(Long strategySaveTime) {
        this.strategySaveTime = strategySaveTime;
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


    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }


    public List<ElectricPricePeriod> getElectricPricePeriod() {
        return electricPricePeriod;
    }


    public void setElectricPricePeriod(List<ElectricPricePeriod> electricPricePeriod) {
        this.electricPricePeriod = electricPricePeriod;
    }


    public DeviceNoTagCodeItem getDeviceNoTagCodeToCheckChargeStatus() {
        return deviceNoTagCodeToCheckChargeStatus;
    }

    public void setDeviceNoTagCodeToCheckChargeStatus(
            DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeStatus) {
        this.deviceNoTagCodeToCheckChargeStatus = deviceNoTagCodeToCheckChargeStatus;
    }


    public int getCheckFactor() {
        return checkFactor;
    }


    public void setCheckFactor(int checkFactor) {
        this.checkFactor = checkFactor;
    }


    public List<DeviceNoTagCodeItem> getDeviceNoTagCodeListToCheckSoc() {
        return deviceNoTagCodeListToCheckSoc;
    }


    public void setDeviceNoTagCodeListToCheckSoc(List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckSoc) {
        this.deviceNoTagCodeListToCheckSoc = deviceNoTagCodeListToCheckSoc;
    }


    public Integer getEssRunStatus() {
        return essRunStatus;
    }


    public void setEssRunStatus(Integer essRunStatus) {
        this.essRunStatus = essRunStatus;
    }


    public Integer getEssAMStatus() {
        return essAMStatus;
    }

    public void setEssAMStatus(Integer essAMStatus) {
        this.essAMStatus = essAMStatus;
    }


    public List<DeviceNoTagCodeItem> getDeviceNoTagCodeListToCalcPrice() {
        return deviceNoTagCodeListToCalcPrice;
    }

    public void setDeviceNoTagCodeListToCalcPrice(
            List<DeviceNoTagCodeItem> deviceNoTagCodeListToCalcPrice) {
        this.deviceNoTagCodeListToCalcPrice = deviceNoTagCodeListToCalcPrice;
    }

    private AirConditionerPeriod airConditionerPeriod;

    public void setAirConditionerPeriod(AirConditionerPeriod airConditionerPeriod) {
        this.airConditionerPeriod = airConditionerPeriod;
    }

    public AirConditionerPeriod getAirConditionerPeriod() {
        return airConditionerPeriod;
    }

    public DeviceNoTagCodeItem getDeviceNoTagCodeToCheckChargeMaxV() {
        return deviceNoTagCodeToCheckChargeMaxV;
    }

    public void setDeviceNoTagCodeToCheckChargeMaxV(DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeMaxV) {
        this.deviceNoTagCodeToCheckChargeMaxV = deviceNoTagCodeToCheckChargeMaxV;
    }

    public DeviceNoTagCodeItem getDeviceNoTagCodeToCheckChargeMinV() {
        return deviceNoTagCodeToCheckChargeMinV;
    }

    public void setDeviceNoTagCodeToCheckChargeMinV(DeviceNoTagCodeItem deviceNoTagCodeToCheckChargeMinV) {
        this.deviceNoTagCodeToCheckChargeMinV = deviceNoTagCodeToCheckChargeMinV;
    }

    public List<DeviceNoTagCodeItem> getDeviceNoTagCodeListToCheckMaxT() {
        return deviceNoTagCodeListToCheckMaxT;
    }

    public void setDeviceNoTagCodeListToCheckMaxT(List<DeviceNoTagCodeItem> deviceNoTagCodeListToCheckMaxT) {
        this.deviceNoTagCodeListToCheckMaxT = deviceNoTagCodeListToCheckMaxT;
    }

    @Override
    public String toString() {
        return "ProjectConfig [id=" + id + ", comment=" + comment + ", projectId="
                + projectId + ", essRunStatus=" + essRunStatus + ", essAMStatus="
                + essAMStatus + ", subProjectId=" + subProjectId + ", config="
                + config + ", electricPricePeriod=" + electricPricePeriod
                + ", deviceNoTagCodeToCheckChargeStatus="
                + deviceNoTagCodeToCheckChargeStatus
                + ", deviceNoTagCodeListToCheckSoc="
                + deviceNoTagCodeListToCheckSoc
                + ", deviceNoTagCodeListToCalcPrice="
                + deviceNoTagCodeListToCalcPrice + ", checkFactor=" + checkFactor
                + "]";
    }


    public int getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(int powerFactor) {
        this.powerFactor = powerFactor;
    }

    public Map<String, Object> getAir() {
        return air;
    }

    public void setAir(Map<String, Object> air) {
        this.air = air;
    }
}
