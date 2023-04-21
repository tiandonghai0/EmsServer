package com.shmet.bean;

import java.util.List;

import org.bson.Document;

public class StatisticsResponse {
    List<Document> result = null;
    int totalCount = 0;

    public List<Document> getResult() {
        return result;
    }

    public void setResult(List<Document> result) {
        this.result = result;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
