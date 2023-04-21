package com.shmet.entity.mongo;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deviceConfig")
public class DeviceConfig {
    @Id
    private String id;
    @NotNull
    private Long deviceNo;
    @NotNull
    private String deviceModel;
    @NotNull
    private String tagCode;
    @NotNull
    private Map<String, Object> config;
    private List<ElectricPricePeriod> incomeElectricPricePeriod;

    public Long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public List<ElectricPricePeriod> getIncomeElectricPricePeriod() {
        return incomeElectricPricePeriod;
    }

    public void setIncomeElectricPricePeriod(List<ElectricPricePeriod> incomeElectricPricePeriod) {
        this.incomeElectricPricePeriod = incomeElectricPricePeriod;
    }

}
