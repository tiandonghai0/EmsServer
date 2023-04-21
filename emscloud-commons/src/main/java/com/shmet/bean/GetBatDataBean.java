package com.shmet.bean;

import java.io.Serializable;


public class GetBatDataBean implements Serializable {
  
    private static final long serialVersionUID = 4434990039844628163L;
    Long acconeId;

    public Long getAcconeId() {
        return acconeId;
    }

    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

}
