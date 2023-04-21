package com.shmet.entity.mysql.gen;

import java.util.Date;
import org.beetl.sql.core.annotatoin.AssignID;

public class TAccone {

    private Long acconeId;
    private Integer createBy;
    private Integer runningStatus;
    private Integer status;
    private Integer updateBy;
    private String acconeSn;
    private Long subProjectId;
    private String ver;
    private Date createDate;
    private Date updateDate;
    private String iccid;
    private String acconeName;

    public TAccone() {
    }

    public String getAcconeName() {
        return acconeName;
    }

    public void setAcconeName(String acconeName) {
        this.acconeName = acconeName;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
    @AssignID
    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Integer runningStatus) {
        this.runningStatus = runningStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public String getAcconeSn() {
        return acconeSn;
    }

    public void setAcconeSn(String acconeSn) {
        this.acconeSn = acconeSn;
    }

    public Long getSubProjectId() {
        return subProjectId;
    }

    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


}
