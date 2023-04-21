package com.shmet.bean;

import java.io.Serializable;

import com.shmet.entity.mysql.gen.TAccone;

public class AcconeStatusBean implements Serializable {
    private static final long serialVersionUID = -5318258546562916826L;
    int connectedStatus = 0;
    int loginStatus = 0;
    long lastLoginTime = 0;
    TAccone tAccone;

    public int getConnectedStatus() {
        return connectedStatus;
    }

    public void setConnectedStatus(int connectedStatus) {
        this.connectedStatus = connectedStatus;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public TAccone gettAccone() {
        return tAccone;
    }

    public void settAccone(TAccone tAccone) {
        this.tAccone = tAccone;
    }

}
