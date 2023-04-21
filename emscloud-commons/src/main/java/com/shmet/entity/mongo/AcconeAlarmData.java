package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "acconeAlarmData")
@CompoundIndexes({
        @CompoundIndex(name = "acconealarm_time_deviceno_tagcode_idx", def = "{'time': -1, 'deviceNo': 1, 'tagCode': 1}")
})
public class AcconeAlarmData {
    @Id
    private String id;

    @NotNull
    private Long deviceNo;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private String tagCode;

    @NotNull
    private Long acconeId;

    @NotNull
    private Long timestamp;

    @NotNull
    private String errorCode;

    @NotNull
    private String errorMsg;

    @NotNull
    private String data;


    static public String getCollectionName() {
        return "acconeAlarmData";
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the deviceNo
     */
    public Long getDeviceNo() {
        return deviceNo;
    }

    /**
     * @param deviceNo the deviceNo to set
     */
    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }

    /**
     * @return the projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the subProjectId
     */
    public Long getSubProjectId() {
        return subProjectId;
    }

    /**
     * @param subProjectId the subProjectId to set
     */
    public void setSubProjectId(Long subProjectId) {
        this.subProjectId = subProjectId;
    }

    /**
     * @return the tagCode
     */
    public String getTagCode() {
        return tagCode;
    }

    /**
     * @param tagCode the tagCode to set
     */
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the acconeId
     */
    public Long getAcconeId() {
        return acconeId;
    }

    /**
     * @param acconeId the acconeId to set
     */
    public void setAcconeId(Long acconeId) {
        this.acconeId = acconeId;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

}
