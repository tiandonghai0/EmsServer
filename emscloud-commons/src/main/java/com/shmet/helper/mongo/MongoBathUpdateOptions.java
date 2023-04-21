package com.shmet.helper.mongo;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class MongoBathUpdateOptions {
    private Query query;
    private Update update;
    private boolean upsert;
    private boolean multi;

    public MongoBathUpdateOptions(Query query, Update update, boolean upsert,
                                  boolean multi) {
        super();
        this.query = query;
        this.update = update;
        this.upsert = upsert;
        this.multi = multi;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public void setUpsert(boolean upsert) {
        this.upsert = upsert;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

}
