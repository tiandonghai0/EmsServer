package com.shmet.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectorInfoBean {

    @JsonProperty("ConnectorID")
    private String connectorID;

    @JsonProperty("ConnectorName")
    private String connectorName;

    @JsonProperty("ConnectorType")
    private Integer connectorType;

    @JsonProperty("VoltageUpperLimits")
    private Integer voltageUpperLimits;

    @JsonProperty("VoltageLowerLimits")
    private Integer voltageLowerLimits;

    @JsonProperty("Current")
    private Integer current;

    @JsonProperty("Power")
    private Double power;

    @JsonProperty("ParkNo")
    private String parkNo;

    @JsonProperty("NationalStandard")
    private Integer nationalStandard;

    public String getConnectorID() {
        return connectorID;
    }

    public void setConnectorID(String connectorID) {
        this.connectorID = connectorID;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public Integer getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(Integer connectorType) {
        this.connectorType = connectorType;
    }

    public Integer getVoltageUpperLimits() {
        return voltageUpperLimits;
    }

    public void setVoltageUpperLimits(Integer voltageUpperLimits) {
        this.voltageUpperLimits = voltageUpperLimits;
    }

    public Integer getVoltageLowerLimits() {
        return voltageLowerLimits;
    }

    public void setVoltageLowerLimits(Integer voltageLowerLimits) {
        this.voltageLowerLimits = voltageLowerLimits;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public String getParkNo() {
        return parkNo;
    }

    public void setParkNo(String parkNo) {
        this.parkNo = parkNo;
    }

    public Integer getNationalStandard() {
        return nationalStandard;
    }

    public void setNationalStandard(Integer nationalStandard) {
        this.nationalStandard = nationalStandard;
    }

    @Override
    public String toString() {
        return "ConnectorInfoBean [connectorID=" + connectorID
                + ", connectorName=" + connectorName + ", connectorType="
                + connectorType + ", voltageUpperLimits=" + voltageUpperLimits
                + ", voltageLowerLimits=" + voltageLowerLimits + ", current="
                + current + ", power=" + power + ", parkNo=" + parkNo
                + ", nationalStandard=" + nationalStandard + "]";
    }


}
