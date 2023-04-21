package com.shmet.entity.mysql.gen;

import java.util.Date;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name="t_device")
public class TDevice   {

    private Long deviceNo ;
    private Integer createBy ;
    private Integer deviceId ;
    private Integer deviceModelType ;
    private Integer status ;
    private Integer updateBy ;
    private Long acconeId ;
    private String deviceModel ;
    private String deviceName ;
    private Long parentDeviceNo ;
    private Long relationDeviceNo ;
    private Long subProjectId ;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate ;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate ;

    public TDevice() {
    }

    public Long getDeviceNo(){
        return  deviceNo;
    }
    public void setDeviceNo(Long deviceNo ){
        this.deviceNo = deviceNo;
    }

    public Integer getCreateBy(){
        return  createBy;
    }
    public void setCreateBy(Integer createBy ){
        this.createBy = createBy;
    }

    public Integer getDeviceId(){
        return  deviceId;
    }
    public void setDeviceId(Integer deviceId ){
        this.deviceId = deviceId;
    }

    public Integer getDeviceModelType(){
        return  deviceModelType;
    }
    public void setDeviceModelType(Integer deviceModelType ){
        this.deviceModelType = deviceModelType;
    }

    public Integer getStatus(){
        return  status;
    }
    public void setStatus(Integer status ){
        this.status = status;
    }

    public Integer getUpdateBy(){
        return  updateBy;
    }
    public void setUpdateBy(Integer updateBy ){
        this.updateBy = updateBy;
    }

    public Long getAcconeId(){
        return  acconeId;
    }
    public void setAcconeId(Long acconeId ){
        this.acconeId = acconeId;
    }

    public String getDeviceModel(){
        return  deviceModel;
    }
    public void setDeviceModel(String deviceModel ){
        this.deviceModel = deviceModel;
    }

    public String getDeviceName(){
        return  deviceName;
    }
    public void setDeviceName(String deviceName ){
        this.deviceName = deviceName;
    }

    public Long getParentDeviceNo(){
        return  parentDeviceNo;
    }
    public void setParentDeviceNo(Long parentDeviceNo ){
        this.parentDeviceNo = parentDeviceNo;
    }

    /**
     * �����豸
     *@return
     */
    public Long getRelationDeviceNo(){
        return  relationDeviceNo;
    }
    /**
     * �����豸
     *@param  relationDeviceNo
     */
    public void setRelationDeviceNo(Long relationDeviceNo ){
        this.relationDeviceNo = relationDeviceNo;
    }

    public Long getSubProjectId(){
        return  subProjectId;
    }
    public void setSubProjectId(Long subProjectId ){
        this.subProjectId = subProjectId;
    }

    public Date getCreateDate(){
        return  createDate;
    }
    public void setCreateDate(Date createDate ){
        this.createDate = createDate;
    }

    public Date getUpdateDate(){
        return  updateDate;
    }
    public void setUpdateDate(Date updateDate ){
        this.updateDate = updateDate;
    }


}

