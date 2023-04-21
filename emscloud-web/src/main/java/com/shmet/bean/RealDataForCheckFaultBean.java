package com.shmet.bean;

import java.util.Date;

public class RealDataForCheckFaultBean {

    private RealDataItem realDataItem;

    /**
     * 存入实时数据时的当前系统时间
     */
    private Date date;

    /**
     * 发送邮件时的当前系统时间
     */
    private Date mailDate;

    public RealDataItem getRealDataItem() {
        return realDataItem;
    }

    public void setRealDataItem(RealDataItem realDataItem) {
        this.realDataItem = realDataItem;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getMailDate() {
        return mailDate;
    }

    public void setMailDate(Date mailDate) {
        this.mailDate = mailDate;
    }

    @Override
    public String toString() {
        return "RealDataForCheckFaultBean [realDataItem=" + realDataItem
                + ", date=" + date + ", mailDate=" + mailDate + "]";
    }


}
