package com.shmet.entity.mysql.gen;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

public class TElectricTimeslot  {
	
	private Long electricTimeslotId ;
	private Integer createBy ;
	private Integer timeslotType ;
	private Integer updateBy ;
	private Long electricPriceId ;
	private Date createDate ;
	private Date endTime ;
	private Date startTime ;
	private Date updateDate ;
	
	public TElectricTimeslot() {
	}
	
	public Long getElectricTimeslotId(){
		return  electricTimeslotId;
	}
	public void setElectricTimeslotId(Long electricTimeslotId ){
		this.electricTimeslotId = electricTimeslotId;
	}
	
	public Integer getCreateBy(){
		return  createBy;
	}
	public void setCreateBy(Integer createBy ){
		this.createBy = createBy;
	}
	
	public Integer getTimeslotType(){
		return  timeslotType;
	}
	public void setTimeslotType(Integer timeslotType ){
		this.timeslotType = timeslotType;
	}
	
	public Integer getUpdateBy(){
		return  updateBy;
	}
	public void setUpdateBy(Integer updateBy ){
		this.updateBy = updateBy;
	}
	
	public Long getElectricPriceId(){
		return  electricPriceId;
	}
	public void setElectricPriceId(Long electricPriceId ){
		this.electricPriceId = electricPriceId;
	}
	
	public Date getCreateDate(){
		return  createDate;
	}
	public void setCreateDate(Date createDate ){
		this.createDate = createDate;
	}
	
	public Date getEndTime(){
		return  endTime;
	}
	public void setEndTime(Date endTime ){
		this.endTime = endTime;
	}
	
	public Date getStartTime(){
		return  startTime;
	}
	public void setStartTime(Date startTime ){
		this.startTime = startTime;
	}
	
	public Date getUpdateDate(){
		return  updateDate;
	}
	public void setUpdateDate(Date updateDate ){
		this.updateDate = updateDate;
	}
	

}
