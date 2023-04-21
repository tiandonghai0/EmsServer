package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "t_device")
public class Device implements Serializable {
    @TableField(value = "device_no")
    private Long deviceNo;
    @TableField(value = "device_id")
    private Integer deviceId;
    @TableField(value = "sub_project_id")
    private Long subProjectId;
    @TableField(value = "device_model")
    private String deviceModel;
    @TableField(value = "device_name")
    private String deviceName;
    @TableField(value = "parent_device_no")
    private Long parentDeviceNo;
    private Integer status;
    @TableField(value = "accone_id")
    private Long acconeId;
    @TableField(value = "create_date")
    private Date createDate;
    @TableField(value = "update_date")
    private Date updateDate;
    @TableField(value = "create_by")
    private Integer createBy;
    @TableField(value = "update_by")
    private Integer updateBy;
    @TableField(value = "device_model_type")
    private Integer deviceModelType;
    @TableField(value = "relation_device_no")
    private Long relationDeviceNo;
    @TableField(value = "hengtong_ispush")
    private Integer hengtongIsPush;

    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setParentDeviceNo(Long parentDeviceNo) {
        this.parentDeviceNo = parentDeviceNo;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public void setDeviceModelType(Integer deviceModelType) {
        this.deviceModelType = deviceModelType;
    }

    public Long getDeviceNo() {
        return deviceNo;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public Long getSubProjectId() {
        return subProjectId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Long getParentDeviceNo() {
        return parentDeviceNo;
    }

    public Integer getStatus() {
        return status;
    }

    public Long getAcconeId() {
        return acconeId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public Integer getDeviceModelType() {
        return deviceModelType;
    }

    public Long getRelationDeviceNo() {
        return relationDeviceNo;
    }

    public void setRelationDeviceNo(Long relationDeviceNo) {
        this.relationDeviceNo = relationDeviceNo;
    }

    public Integer getHengtongIsPush() {
        return hengtongIsPush;
    }

    public void setHengtongIsPush(Integer hengtongIsPush) {
        this.hengtongIsPush = hengtongIsPush;
    }
}
