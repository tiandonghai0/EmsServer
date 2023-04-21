package com.shmet.bean;

import java.io.Serializable;

public class UpdateAcconeBean implements Serializable {
    private static final long serialVersionUID = -5318258546562916826L;
    Long acconeId;
    String url;
    String ver;
    int status;

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
