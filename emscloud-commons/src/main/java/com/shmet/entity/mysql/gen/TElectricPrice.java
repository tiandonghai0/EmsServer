package com.shmet.entity.mysql.gen;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

public class TElectricPrice  {
	
	private Long electricPriceId ;
	private Integer createBy ;
	private Integer updateBy ;
	private BigDecimal flatPrice ;
	private BigDecimal peakPrice ;
	private String remarks ;
	private BigDecimal valleyPrice ;
	private Date createDate ;
	private Date updateDate ;
	
	public TElectricPrice() {
	}
	
	public Long getElectricPriceId(){
		return  electricPriceId;
	}
	public void setElectricPriceId(Long electricPriceId ){
		this.electricPriceId = electricPriceId;
	}
	
	public Integer getCreateBy(){
		return  createBy;
	}
	public void setCreateBy(Integer createBy ){
		this.createBy = createBy;
	}
	
	public Integer getUpdateBy(){
		return  updateBy;
	}
	public void setUpdateBy(Integer updateBy ){
		this.updateBy = updateBy;
	}
	
	public BigDecimal getFlatPrice(){
		return  flatPrice;
	}
	public void setFlatPrice(BigDecimal flatPrice ){
		this.flatPrice = flatPrice;
	}
	
	public BigDecimal getPeakPrice(){
		return  peakPrice;
	}
	public void setPeakPrice(BigDecimal peakPrice ){
		this.peakPrice = peakPrice;
	}
	
	public String getRemarks(){
		return  remarks;
	}
	public void setRemarks(String remarks ){
		this.remarks = remarks;
	}
	
	public BigDecimal getValleyPrice(){
		return  valleyPrice;
	}
	public void setValleyPrice(BigDecimal valleyPrice ){
		this.valleyPrice = valleyPrice;
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
