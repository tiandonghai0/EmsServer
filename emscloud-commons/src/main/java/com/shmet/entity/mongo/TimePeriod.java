package com.shmet.entity.mongo;


public class TimePeriod {
    private Integer timeFrom;
    private Integer timeTo;
    // 1:peak, 2:valley, 3:flat
    private Integer type;
    private String typeName;
    private Integer chargeType;
    private String chargeTypeName;
    private Double chargeP;
    private String handler;
    private Double endSoc;
    private Double elecPrice;
    private Double svrPrice;

    public Integer getTimeFrom() {
        return timeFrom;
    }


    public void setTimeFrom(Integer timeFrom) {
        this.timeFrom = timeFrom;
    }


    public Integer getTimeTo() {
        return timeTo;
    }


    public void setTimeTo(Integer timeTo) {
        this.timeTo = timeTo;
    }


    public Integer getType() {
        return type;
    }


    public void setType(Integer type) {
        this.type = type;
    }


    public String getTypeName() {
        return typeName;
    }


    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    public Integer getChargeType() {
        return chargeType;
    }


    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }


    public String getChargeTypeName() {
        return chargeTypeName;
    }


    public void setChargeTypeName(String chargeTypeName) {
        this.chargeTypeName = chargeTypeName;
    }


    public Double getChargeP() {
        return chargeP;
    }


    public void setChargeP(Double chargeP) {
        this.chargeP = chargeP;
    }


    public String getHandler() {
        return handler;
    }


    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Double getElecPrice() {
        return elecPrice;
    }


    public void setElecPrice(Double elecPrice) {
        this.elecPrice = elecPrice;
    }

    public Double getSvrPrice() {
        return svrPrice;
    }

    public void setSvrPrice(Double svrPrice) {
        this.svrPrice = svrPrice;
    }

    public Double getEndSoc() {
        return endSoc;
    }

    public void setEndSoc(Double endSoc) {
        this.endSoc = endSoc;
    }
}
