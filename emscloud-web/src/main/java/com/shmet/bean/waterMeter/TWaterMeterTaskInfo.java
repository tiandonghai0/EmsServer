package com.shmet.bean.waterMeter;

import com.shmet.entity.mysql.gen.TWaterMeterTask;

public class TWaterMeterTaskInfo extends TWaterMeterTask {
    private Integer pageNo;
    private Integer pageSize;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
