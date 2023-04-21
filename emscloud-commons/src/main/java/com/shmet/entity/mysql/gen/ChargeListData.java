package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "t_charge_list_data")
public class ChargeListData implements Serializable {
    public Integer id;
    @TableField(value = "project_no")
    private String projectNo;
    @TableField(value = "line_no")
    private String lineNo;
    @TableField(value = "call_date")
    private String callDate;
    @TableField(value = "feng_val")
    private Double fengVal;
    @TableField(value = "ping1_val")
    private Double ping1Val;
    @TableField(value = "ping2_val")
    private Double ping2Val;
    @TableField(value = "gu_val")
    private Double guVal;
    @TableField(value = "contract_md")
    private Integer contractMd;
    @TableField(value = "feng_md")
    private Integer fengMd;
    @TableField(value = "ping1_md")
    private Integer ping1Md;
    @TableField(value = "ping2_md")
    private Integer ping2Md;
    @TableField(value = "gu_md")
    private Integer guMd;
    @TableField(value = "exceed_md")
    private Integer exceedMd;
    @TableField(value = "base1_price")
    private Double base1Price;
    @TableField(value = "base2_price")
    private Double base2Price;
    @TableField(value = "feng_price")
    private Double fengPrice;
    @TableField(value = "ping_price")
    private Double pingPrice;
    @TableField(value = "gu_price")
    private Double guPrice;
    private Double sum;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public void setFengVal(Double fengVal) {
        this.fengVal = fengVal;
    }

    public void setPing1Val(Double ping1Val) {
        this.ping1Val = ping1Val;
    }

    public void setPing2Val(Double ping2Val) {
        this.ping2Val = ping2Val;
    }

    public void setGuVal(Double guVal) {
        this.guVal = guVal;
    }

    public void setContractMd(Integer contractMd) {
        this.contractMd = contractMd;
    }

    public void setFengMd(Integer fengMd) {
        this.fengMd = fengMd;
    }

    public void setPing1Md(Integer ping1Md) {
        this.ping1Md = ping1Md;
    }

    public void setPing2Md(Integer ping2Md) {
        this.ping2Md = ping2Md;
    }

    public void setGuMd(Integer guMd) {
        this.guMd = guMd;
    }

    public void setExceedMd(Integer exceedMd) {
        this.exceedMd = exceedMd;
    }

    public void setBase1Price(Double base1Price) {
        this.base1Price = base1Price;
    }

    public void setBase2Price(Double base2Price) {
        this.base2Price = base2Price;
    }

    public void setFengPrice(Double fengPrice) {
        this.fengPrice = fengPrice;
    }

    public void setPingPrice(Double pingPrice) {
        this.pingPrice = pingPrice;
    }

    public void setGuPrice(Double guPrice) {
        this.guPrice = guPrice;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Integer getId() {
        return id;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public String getLineNo() {
        return lineNo;
    }

    public String getCallDate() {
        return callDate;
    }

    public Double getFengVal() {
        return fengVal;
    }

    public Double getPing1Val() {
        return ping1Val;
    }

    public Double getPing2Val() {
        return ping2Val;
    }

    public Double getGuVal() {
        return guVal;
    }

    public Integer getContractMd() {
        return contractMd;
    }

    public Integer getFengMd() {
        return fengMd;
    }

    public Integer getPing1Md() {
        return ping1Md;
    }

    public Integer getPing2Md() {
        return ping2Md;
    }

    public Integer getGuMd() {
        return guMd;
    }

    public Integer getExceedMd() {
        return exceedMd;
    }

    public Double getBase1Price() {
        return base1Price;
    }

    public Double getBase2Price() {
        return base2Price;
    }

    public Double getFengPrice() {
        return fengPrice;
    }

    public Double getPingPrice() {
        return pingPrice;
    }

    public Double getGuPrice() {
        return guPrice;
    }

    public Double getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "ChargeListData{" +
                "id=" + id +
                ", projectNo='" + projectNo + '\'' +
                ", lineNo='" + lineNo + '\'' +
                ", callDate='" + callDate + '\'' +
                ", fengVal=" + fengVal +
                ", ping1Val=" + ping1Val +
                ", ping2Val=" + ping2Val +
                ", guVal=" + guVal +
                ", contractMd=" + contractMd +
                ", fengMd=" + fengMd +
                ", ping1Md=" + ping1Md +
                ", ping2Md=" + ping2Md +
                ", guMd=" + guMd +
                ", exceedMd=" + exceedMd +
                ", base1Price=" + base1Price +
                ", base2Price=" + base2Price +
                ", fengPrice=" + fengPrice +
                ", pingPrice=" + pingPrice +
                ", guPrice=" + guPrice +
                ", sum=" + sum +
                '}';
    }
}
