package com.shmet.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EquipmentInfoBean {

    @JsonProperty("EquipmentID")
    private String equipmentID;

    @JsonProperty("EquipmentName")
    private String equipmentName;

    @JsonProperty("ManufacturerID")
    private String manufacturerID;

    @JsonProperty("ManufacturerName")
    private String manufacturerName;

    @JsonProperty("EquipmentModel")
    private String equipmentModel;

    @JsonProperty("ProductionDate")
    private String productionDate;

    @JsonProperty("EquipmentType")
    private Integer equipmentType;

    @JsonProperty("EquipmentLng")
    private Double equipmentLng;

    @JsonProperty("EquipmentLat")
    private Double equipmentLat;

    @JsonProperty("Power")
    private Double power;

    @JsonProperty("ConnectorInfos")
    private List<ConnectorInfoBean> connectorInfos;

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getManufacturerID() {
        return manufacturerID;
    }

    public void setManufacturerID(String manufacturerID) {
        this.manufacturerID = manufacturerID;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public Integer getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(Integer equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Double getEquipmentLng() {
        return equipmentLng;
    }

    public void setEquipmentLng(Double equipmentLng) {
        this.equipmentLng = equipmentLng;
    }

    public Double getEquipmentLat() {
        return equipmentLat;
    }

    public void setEquipmentLat(Double equipmentLat) {
        this.equipmentLat = equipmentLat;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public List<ConnectorInfoBean> getConnectorInfos() {
        return connectorInfos;
    }

    public void setConnectorInfos(List<ConnectorInfoBean> connectorInfos) {
        this.connectorInfos = connectorInfos;
    }

    @Override
    public String toString() {
        return "EquipmentInfoBean [equipmentID=" + equipmentID
                + ", equipmentName=" + equipmentName + ", manufacturerID="
                + manufacturerID + ", manufacturerName=" + manufacturerName
                + ", equipmentModel=" + equipmentModel + ", productionDate="
                + productionDate + ", equipmentType=" + equipmentType
                + ", equipmentLng=" + equipmentLng + ", equipmentLat="
                + equipmentLat + ", power=" + power + ", connectorInfos="
                + connectorInfos + "]";
    }


}
