package com.shmet.entity.mysql.gen;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.beetl.sql.core.annotatoin.Table;
import org.springframework.format.annotation.DateTimeFormat;


@Table(name="t_water_meter")
public class TWaterMeter   {
	
	private Integer fId ;
	private Integer fWaterMeterNumber ;
	private Integer fWaterYield ;
	private String fCreateUser ;
	private String fUpdateUser ;
	private String fProjectNo ;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fCreateTime ;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fUpdateTime ;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fLastCloseTime ;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fLastOpenTime ;
	
	public TWaterMeter() {
	}
	
	public Integer getfId(){
		return  fId;
	}
	public void setfId(Integer fId ){
		this.fId = fId;
	}

	public Integer getfWaterMeterNumber(){
		return  fWaterMeterNumber;
	}

	public void setfWaterMeterNumber(Integer fWaterMeterNumber ){
		this.fWaterMeterNumber = fWaterMeterNumber;
	}

	public Integer getfWaterYield(){
		return  fWaterYield;
	}

	public void setfWaterYield(Integer fWaterYield ){
		this.fWaterYield = fWaterYield;
	}
	
	public String getfCreateUser(){
		return  fCreateUser;
	}
	public void setfCreateUser(String fCreateUser ){
		this.fCreateUser = fCreateUser;
	}
	
	public String getfUpdateUser(){
		return  fUpdateUser;
	}
	public void setfUpdateUser(String fUpdateUser ){
		this.fUpdateUser = fUpdateUser;
	}

	public String getfProjectNo(){
		return  fProjectNo;
	}

	public void setfProjectNo(String fProjectNo ){
		this.fProjectNo = fProjectNo;
	}
	
	public Date getfCreateTime(){
		return  fCreateTime;
	}
	public void setfCreateTime(Date fCreateTime ){
		this.fCreateTime = fCreateTime;
	}
	
	public Date getfUpdateTime(){
		return  fUpdateTime;
	}
	public void setfUpdateTime(Date fUpdateTime ){
		this.fUpdateTime = fUpdateTime;
	}
	

	public Date getfLastCloseTime(){
		return  fLastCloseTime;
	}

	public void setfLastCloseTime(Date fLastCloseTime ){
		this.fLastCloseTime = fLastCloseTime;
	}

	public Date getfLastOpenTime(){
		return  fLastOpenTime;
	}

	public void setfLastOpenTime(Date fLastOpenTime ){
		this.fLastOpenTime = fLastOpenTime;
	}
	

}
