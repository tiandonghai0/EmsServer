package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chargeEvent")
@CompoundIndexes({
        @CompoundIndex(name = "chargeEvent_sendTime_idx", def = "{'sendTime': -1}")
})
public class ChargeEvent {
    @Id
    private String id;

    @NotNull
    private Integer projectId;

    @NotNull
    private Long subProjectId;

    @NotNull
    private Long acconeId;

    @NotNull
    // 1:充电, 2:放电, 3:停止
            Integer chargeType;

    @NotNull
    // 0:手动, 1:自动
            Integer essAMStatus;

    @NotNull
    // yyyyMMddHHmmss
    private Long startTime;

    // yyyyMMddHHmmss
    private Long endTime;

    @NotNull
    // yyyyMMddHHmmss
    private Long sendTime;

    private Long confirmTime;

    @NotNull
    private boolean isConfirmed;

    private Double startEPX;

    private Double endEPX;

    @NotNull
    private Integer senderId;

    @NotNull
    private Double settingP;

    @NotNull
    private Double startSoc;

    private Double endSoc;

    private Integer esNo;//储能编号

    private String content;

    private String result;

    public Integer getEsNo() {
        return esNo;
    }

    public void setEsNo(Integer esNo) {
        this.esNo = esNo;
    }

    static public String getCollectionName() {
        return "chargeEvent";
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
     * @return the chargeType
     */
    public Integer getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType the chargeType to set
     */
    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    /**
     * @return the essAMStatus
     */
    public Integer getEssAMStatus() {
        return essAMStatus;
    }

    /**
     * @param essAMStatus the essAMStatus to set
     */
    public void setEssAMStatus(Integer essAMStatus) {
        this.essAMStatus = essAMStatus;
    }

    /**
     * @return the startTime
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the sendTime
     */
    public Long getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime the sendTime to set
     */
    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return the confirmTime
     */
    public Long getConfirmTime() {
        return confirmTime;
    }

    /**
     * @param confirmTime the confirmTime to set
     */
    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    /**
     * @return the isConfirmed
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }

    /**
     * @param isConfirmed the isConfirmed to set
     */
    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    /**
     * @return the startEPX
     */
    public Double getStartEPX() {
        return startEPX;
    }

    /**
     * @param startEPX the startEPX to set
     */
    public void setStartEPX(Double startEPX) {
        this.startEPX = startEPX;
    }

    /**
     * @return the endEPX
     */
    public Double getEndEPX() {
        return endEPX;
    }

    /**
     * @param endEPX the endEPX to set
     */
    public void setEndEPX(Double endEPX) {
        this.endEPX = endEPX;
    }

    /**
     * @return the senderId
     */
    public Integer getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the settingP
     */
    public Double getSettingP() {
        return settingP;
    }

    /**
     * @param settingP the settingP to set
     */
    public void setSettingP(Double settingP) {
        this.settingP = settingP;
    }

    /**
     * @return the startSoc
     */
    public Double getStartSoc() {
        return startSoc;
    }

    /**
     * @param startSoc the startSoc to set
     */
    public void setStartSoc(Double startSoc) {
        this.startSoc = startSoc;
    }

    /**
     * @return the endSoc
     */
    public Double getEndSoc() {
        return endSoc;
    }

    /**
     * @param endSoc the endSoc to set
     */
    public void setEndSoc(Double endSoc) {
        this.endSoc = endSoc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
