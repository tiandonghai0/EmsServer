package com.shmet.vo.req;

/**
 * @author
 */
public class AdQueryReq {
  //分页查询 页码 不传默认为 1
  private Integer pageNum = 1;
  //分页查询 每页大小 不传默认为 10
  private Integer pageSize = 10;
  //船只名称
  private String shipName;
  //靠泊时间
  private String berthTime;

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getShipName() {
    return shipName;
  }

  public void setShipName(String shipName) {
    this.shipName = shipName;
  }

  public String getBerthTime() {
    return berthTime;
  }

  public void setBerthTime(String berthTime) {
    this.berthTime = berthTime;
  }
}
