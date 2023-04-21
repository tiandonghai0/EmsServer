package com.shmet.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StationsInfoDataBean {

    @JsonProperty("StationInfos")
    private List<StationInfoBean> stationInfos;

    @JsonProperty("PageNo")
    private Integer pageNo;

    @JsonProperty("PageCount")
    private Integer pageCount;

    @JsonProperty("ItemSize")
    private Integer itemSize;

    public List<StationInfoBean> getStationInfosBean() {
        return stationInfos;
    }

    public void setStationInfosBean(List<StationInfoBean> stationInfoBean) {
        this.stationInfos = stationInfoBean;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getItemSize() {
        return itemSize;
    }

    public void setItemSize(Integer itemSize) {
        this.itemSize = itemSize;
    }

    @Override
    public String toString() {
        return "StationsInfoData [stationInfosBean=" + stationInfos
                + ", pageNo=" + pageNo + ", pageCount=" + pageCount
                + ", itemSize=" + itemSize + "]";
    }


}
