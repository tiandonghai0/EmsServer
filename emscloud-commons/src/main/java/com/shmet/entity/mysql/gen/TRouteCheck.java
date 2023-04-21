package com.shmet.entity.mysql.gen;

import java.util.Date;

public class TRouteCheck {

    private Integer id;
    private Integer createBy;
    //设备状态 1-良好，2-报修，
    private Integer deviceState;
    private Integer projectId;
    private Integer updateBy;
    private String checkman;
    private String details;
    private Long deviceNo;
    private Date createDate;
    private Date updateDate;

    public TRouteCheck() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * 设备状态 1-良好，2-报修，
     *
     * @return
     */
    public Integer getDeviceState() {
        return deviceState;
    }

    /**
     * 设备状态 1-良好，2-报修，
     *
     * @param deviceState
     */
    public void setDeviceState(Integer deviceState) {
        this.deviceState = deviceState;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public String getCheckman() {
        return checkman;
    }

    public void setCheckman(String checkman) {
        this.checkman = checkman;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
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
