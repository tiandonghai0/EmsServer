package com.shmet.entity.mongo;

import javax.validation.constraints.NotNull;

public class ChargeEventDetail {

    private Integer senderId;

    @NotNull
    // yyyyMMddHHmmss
    private Long sendTime;

    @NotNull
    private Double settingP;

    @NotNull
    private Double soc;

    @NotNull
    private Long eventDetailId;

    @NotNull
    private Long confirmTime;

    @NotNull
    private boolean isConfirmed;

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
     * @return the soc
     */
    public Double getSoc() {
        return soc;
    }

    /**
     * @param soc the soc to set
     */
    public void setSoc(Double soc) {
        this.soc = soc;
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
    public boolean getIsConfirmed() {
        return isConfirmed;
    }

    /**
     * @param isConfirmed the isConfirmed to set
     */
    public void setIsConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
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
     * @param isConfirmed the isConfirmed to set
     */
    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    /**
     * @return the eventDetailId
     */
    public Long getEventDetailId() {
        return eventDetailId;
    }

    /**
     * @param eventDetailId the eventDetailId to set
     */
    public void setEventDetailId(Long eventDetailId) {
        this.eventDetailId = eventDetailId;
    }

}
