package com.shmet.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StationInfoBean {

    @JsonProperty("EquipmentInfos")
    private List<EquipmentInfoBean> equipmentInfos;

    @JsonProperty("StationID")
    private String stationID;

    @JsonProperty("OperatorID")
    private String operatorID;

    @JsonProperty("EquipmentOwnerID")
    private String equipmentOwnerID;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("AreaCode")
    private String areaCode;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("StationTel")
    private String stationTel;

    @JsonProperty("ServiceTel")
    private String serviceTel;

    @JsonProperty("StationType")
    private Integer stationType;

    @JsonProperty("StationStatus")
    private Integer stationStatus;

    @JsonProperty("ParkNums")
    private Integer parkNums;

    @JsonProperty("StationLng")
    private Integer stationLng;

    @JsonProperty("StationLat")
    private Integer stationLat;

    @JsonProperty("SiteGuide")
    private String siteGuide;

    @JsonProperty("Construction")
    private Integer construction;

    @JsonProperty("Pictures")
    private List<String> pictures;

    @JsonProperty("MatchCars")
    private String matchCars;

    @JsonProperty("ParkInfo")
    private String parkInfo;

    @JsonProperty("BusineHours")
    private String busineHours;

    @JsonProperty("ElectricityFee")
    private String electricityFee;

    @JsonProperty("ServiceFee")
    private String serviceFee;

    @JsonProperty("ParkFee")
    private String parkFee;

    @JsonProperty("Payment")
    private String payment;

    @JsonProperty("SupportOrder")
    private Integer supportOrder;

    @JsonProperty("Remark")
    private String remark;

    public StationInfoBean() {
        super();
    }

    public StationInfoBean(List<EquipmentInfoBean> equipmentInfos,
                           String stationID, String operatorID, String equipmentOwnerID,
                           String stationName, String countryCode, String areaCode,
                           String address, String stationTel, String serviceTel,
                           Integer stationType, Integer stationStatus, Integer parkNums,
                           Integer stationLng, Integer stationLat, String siteGuide,
                           Integer construction, List<String> pictures, String matchCars,
                           String parkInfo, String busineHours, String electricityFee,
                           String serviceFee, String parkFee, String payment,
                           Integer supportOrder, String remark) {
        super();
        this.equipmentInfos = equipmentInfos;
        this.stationID = stationID;
        this.operatorID = operatorID;
        this.equipmentOwnerID = equipmentOwnerID;
        this.stationName = stationName;
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.address = address;
        this.stationTel = stationTel;
        this.serviceTel = serviceTel;
        this.stationType = stationType;
        this.stationStatus = stationStatus;
        this.parkNums = parkNums;
        this.stationLng = stationLng;
        this.stationLat = stationLat;
        this.siteGuide = siteGuide;
        this.construction = construction;
        this.pictures = pictures;
        this.matchCars = matchCars;
        this.parkInfo = parkInfo;
        this.busineHours = busineHours;
        this.electricityFee = electricityFee;
        this.serviceFee = serviceFee;
        this.parkFee = parkFee;
        this.payment = payment;
        this.supportOrder = supportOrder;
        this.remark = remark;
    }

    public List<EquipmentInfoBean> getEquipmentInfos() {
        return equipmentInfos;
    }

    public void setEquipmentInfos(List<EquipmentInfoBean> equipmentInfos) {
        this.equipmentInfos = equipmentInfos;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getEquipmentOwnerID() {
        return equipmentOwnerID;
    }

    public void setEquipmentOwnerID(String equipmentOwnerID) {
        this.equipmentOwnerID = equipmentOwnerID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStationTel() {
        return stationTel;
    }

    public void setStationTel(String stationTel) {
        this.stationTel = stationTel;
    }

    public String getServiceTel() {
        return serviceTel;
    }

    public void setServiceTel(String serviceTel) {
        this.serviceTel = serviceTel;
    }

    public Integer getStationType() {
        return stationType;
    }

    public void setStationType(Integer stationType) {
        this.stationType = stationType;
    }

    public Integer getStationStatus() {
        return stationStatus;
    }

    public void setStationStatus(Integer stationStatus) {
        this.stationStatus = stationStatus;
    }

    public Integer getParkNums() {
        return parkNums;
    }

    public void setParkNums(Integer parkNums) {
        this.parkNums = parkNums;
    }

    public Integer getStationLng() {
        return stationLng;
    }

    public void setStationLng(Integer stationLng) {
        this.stationLng = stationLng;
    }

    public Integer getStationLat() {
        return stationLat;
    }

    public void setStationLat(Integer stationLat) {
        this.stationLat = stationLat;
    }

    public String getSiteGuide() {
        return siteGuide;
    }

    public void setSiteGuide(String siteGuide) {
        this.siteGuide = siteGuide;
    }

    public Integer getConstruction() {
        return construction;
    }

    public void setConstruction(Integer construction) {
        this.construction = construction;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getMatchCars() {
        return matchCars;
    }

    public void setMatchCars(String matchCars) {
        this.matchCars = matchCars;
    }

    public String getParkInfo() {
        return parkInfo;
    }

    public void setParkInfo(String parkInfo) {
        this.parkInfo = parkInfo;
    }

    public String getBusineHours() {
        return busineHours;
    }

    public void setBusineHours(String busineHours) {
        this.busineHours = busineHours;
    }

    public String getElectricityFee() {
        return electricityFee;
    }

    public void setElectricityFee(String electricityFee) {
        this.electricityFee = electricityFee;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getParkFee() {
        return parkFee;
    }

    public void setParkFee(String parkFee) {
        this.parkFee = parkFee;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Integer getSupportOrder() {
        return supportOrder;
    }

    public void setSupportOrder(Integer supportOrder) {
        this.supportOrder = supportOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "StationInfoBean [equipmentInfos=" + equipmentInfos
                + ", stationID=" + stationID + ", operatorID=" + operatorID
                + ", equipmentOwnerID=" + equipmentOwnerID + ", stationName="
                + stationName + ", countryCode=" + countryCode + ", areaCode="
                + areaCode + ", address=" + address + ", stationTel="
                + stationTel + ", serviceTel=" + serviceTel + ", stationType="
                + stationType + ", stationStatus=" + stationStatus
                + ", parkNums=" + parkNums + ", stationLng=" + stationLng
                + ", stationLat=" + stationLat + ", siteGuide=" + siteGuide
                + ", construction=" + construction + ", pictures=" + pictures
                + ", matchCars=" + matchCars + ", parkInfo=" + parkInfo
                + ", busineHours=" + busineHours + ", electricityFee="
                + electricityFee + ", serviceFee=" + serviceFee + ", parkFee="
                + parkFee + ", payment=" + payment + ", supportOrder="
                + supportOrder + ", remark=" + remark + "]";
    }


}
