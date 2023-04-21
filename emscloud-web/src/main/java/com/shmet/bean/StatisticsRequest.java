package com.shmet.bean;

import java.util.List;

public class StatisticsRequest {
  public static final int SORT_TIME_DEVICENO = 1;
  public static final int SORT_DEVICENO_TIME = 2;

  int sortFlag = 0;
  int pageNum = -1;
  int pageCount = -1;
  int startTime = Integer.MIN_VALUE;
  int endTime = Integer.MAX_VALUE;

  //子项目id
  long subProjectId = 0;

  List<String> excludeTagCodeList;

  List<StatisticsRequestItem> statisticsRequestItemList;

  /**
   * 是否生成报表
   */
  public int isReport;

  public int getSortFlag() {
    return sortFlag;
  }

  public void setSortFlag(int sortFlag) {
    this.sortFlag = sortFlag;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public List<StatisticsRequestItem> getStatisticsRequestItemList() {
    return statisticsRequestItemList;
  }

  public void setStatisticsRequestItemList(List<StatisticsRequestItem> statisticsRequestItemList) {
    this.statisticsRequestItemList = statisticsRequestItemList;
  }

  public int getStartTime() {
    return startTime;
  }

  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  public int getEndTime() {
    return endTime;
  }

  public void setEndTime(int endTime) {
    this.endTime = endTime;
  }

  public long getSubProjectId() {
    return subProjectId;
  }

  public void setSubProjectId(long subProjectId) {
    this.subProjectId = subProjectId;
  }

  public List<String> getExcludeTagCodeList() {
    return excludeTagCodeList;
  }

  public void setExcludeTagCodeList(List<String> excludeTagCodeList) {
    this.excludeTagCodeList = excludeTagCodeList;
  }

  public int getIsReport() {
    return isReport;
  }

  public void setIsReport(int isReport) {
    this.isReport = isReport;
  }

}
