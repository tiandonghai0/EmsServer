package com.shmet.bean;

import java.util.List;

public class RealDataMonitorBean {
    String clientId;
    List<RealDataItem> realDataItemList;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<RealDataItem> getRealDataItemList() {
        return realDataItemList;
    }

    public void setRealDataItemList(List<RealDataItem> realDataItemList) {
        this.realDataItemList = realDataItemList;
    }

}
