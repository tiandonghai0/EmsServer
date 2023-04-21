package com.shmet.entity.mongo;

import java.util.List;

public class ChargePolicy {
    private Integer dayOfWeekFrom;
    private Integer dayOfWeekTo;
    private List<TimePeriod> policy;

    /**
     * @return the dayOfWeekFrom
     */
    public Integer getDayOfWeekFrom() {
        return dayOfWeekFrom;
    }

    /**
     * @param dayOfWeekFrom the dayOfWeekFrom to set
     */
    public void setDayOfWeekFrom(Integer dayOfWeekFrom) {
        this.dayOfWeekFrom = dayOfWeekFrom;
    }

    /**
     * @return the dayOfWeekTo
     */
    public Integer getDayOfWeekTo() {
        return dayOfWeekTo;
    }

    /**
     * @param dayOfWeekTo the dayOfWeekTo to set
     */
    public void setDayOfWeekTo(Integer dayOfWeekTo) {
        this.dayOfWeekTo = dayOfWeekTo;
    }

    /**
     * @return the policy
     */
    public List<TimePeriod> getPolicy() {
        return policy;
    }

    /**
     * @param policy the policy to set
     */
    public void setPolicy(List<TimePeriod> policy) {
        this.policy = policy;
    }

}
