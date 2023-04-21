package com.shmet.entity.mysql.gen;

import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;


/* 
* 
* gen by beetlsql 2020-09-17
*/
@Table(name="t_on_off_network_record")
public class TOnOffNetworkRecord {
	
	private Integer fId ;
	private Long fDeviceNo ;
	private Long fSubProjectId ;
	private Date fCreateTime ;
	private Date fUpdateTime ;
	private Long fOffTime ;
	private Long fOnTime ;
	private Long fAmmeterDeviceNo;
	
	public TOnOffNetworkRecord() {
	}
	
	public Integer getfId(){
		return  fId;
	}
	public void setfId(Integer fId ){
		this.fId = fId;
	}
	
	public Long getfDeviceNo(){
		return  fDeviceNo;
	}
	public void setfDeviceNo(Long fDeviceNo ){
		this.fDeviceNo = fDeviceNo;
	}
	
	public Long getfSubProjectId(){
		return  fSubProjectId;
	}
	public void setfSubProjectId(Long fSubProjectId ){
		this.fSubProjectId = fSubProjectId;
	}
	
	public Date getfUpdateTime(){
		return  fUpdateTime;
	}
	public void setfUpdateTime(Date fUpdateTime ){
		this.fUpdateTime = fUpdateTime;
	}

	public Date getfCreateTime(){
		return  fCreateTime;
	}
	public void setfCreateTime(Date fCreateTime ){
		this.fCreateTime = fCreateTime;
	}

	public Long getfOffTime(){
		return  fOffTime;
	}
	public void setfOffTime(Long fOffTime ){
		this.fOffTime = fOffTime;
	}
	
	public Long getfOnTime(){
		return  fOnTime;
	}
	public void setfOnTime(Long fOnTime ){
		this.fOnTime = fOnTime;
	}

	public Long getfAmmeterDeviceNo() {
		return fAmmeterDeviceNo;
	}

	public void setfAmmeterDeviceNo(Long fAmmeterDeviceNo) {
		this.fAmmeterDeviceNo = fAmmeterDeviceNo;
	}
}
