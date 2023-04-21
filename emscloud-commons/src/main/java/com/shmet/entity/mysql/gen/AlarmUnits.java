package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "t_alarm_units")
public class AlarmUnits implements Serializable {
    private Integer id;
    private String tagCode;
    private String alarmName;
    private String levelTitle;
    private String projectNo;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public Integer getId() {
        return id;
    }

    public String getTagCode() {
        return tagCode;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public String getProjectNo() {
        return projectNo;
    }

    @Override
    public String toString() {
        return "AlarmUnits{" +
                "id=" + id +
                ", tagCode='" + tagCode + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", levelTitle='" + levelTitle + '\'' +
                ", projectNo='" + projectNo + '\'' +
                '}';
    }
}
