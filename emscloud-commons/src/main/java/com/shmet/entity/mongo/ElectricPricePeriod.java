package com.shmet.entity.mongo;

import java.util.List;
import javax.validation.constraints.NotNull;

public class ElectricPricePeriod {

    private Integer dateFrom;
    private Integer dateTo;

    @NotNull
    private String periodName;

    @NotNull
    private List<SeasonPeriod> seasonPeriod;

    @NotNull
    private List<TimePeriod> timePeriod;

    @NotNull
    private List<ChargePolicy> chargePolicy;

    @NotNull
    private List<ChargePolicy> chargePolicyClazz;

    @NotNull
    private Double peakPrice;

    @NotNull
    private Double valleyPrice;

    @NotNull
    private Double flatPrice;

    private Double aiguillePrice;


    public List<SeasonPeriod> getSeasonPeriod() {
        return seasonPeriod;
    }


    public void setSeasonPeriod(List<SeasonPeriod> seasonPeriod) {
        this.seasonPeriod = seasonPeriod;
    }


    public List<TimePeriod> getTimePeriod() {
        return timePeriod;
    }


    public void setTimePeriod(List<TimePeriod> timePeriod) {
        this.timePeriod = timePeriod;
    }


    public Double getPeakPrice() {
        return peakPrice;
    }


    public void setPeakPrice(Double peakPrice) {
        this.peakPrice = peakPrice;
    }


    public Double getValleyPrice() {
        return valleyPrice;
    }


    public void setValleyPrice(Double valleyPrice) {
        this.valleyPrice = valleyPrice;
    }


    public Double getFlatPrice() {
        return flatPrice;
    }


    public void setFlatPrice(Double flatPrice) {
        this.flatPrice = flatPrice;
    }


    public String getPeriodName() {
        return periodName;
    }


    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }


    public List<ChargePolicy> getChargePolicy() {
        return chargePolicy;
    }


    public void setChargePolicy(List<ChargePolicy> chargePolicy) {
        this.chargePolicy = chargePolicy;
    }


    public List<ChargePolicy> getChargePolicyClazz() {
        return chargePolicyClazz;
    }


    public void setChargePolicyClazz(List<ChargePolicy> chargePolicyClazz) {
        this.chargePolicyClazz = chargePolicy;
    }


    public Integer getDateFrom() {
        return dateFrom;
    }


    public void setDateFrom(Integer dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Integer getDateTo() {
        return dateTo;
    }

    public void setDateTo(Integer dateTo) {
        this.dateTo = dateTo;
    }

    public Double getAiguillePrice() {
        return aiguillePrice;
    }

    public void setAiguillePrice(Double aiguillePrice) {
        this.aiguillePrice = aiguillePrice;
    }
}
