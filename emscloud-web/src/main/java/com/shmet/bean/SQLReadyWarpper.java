package com.shmet.bean;

import org.beetl.sql.core.SQLReady;


public class SQLReadyWarpper {
    Object[] args;
    String sql = null;

    public SQLReady getSQLReadyInstance() {
        SQLReady sqlReady = new SQLReady(sql);
        if (args != null && args.length > 0) {
            sqlReady.setArgs(args);
        }
        return sqlReady;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
