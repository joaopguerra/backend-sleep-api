package com.noom.interview.fullstack.sleep.dto;

import java.util.Map;

public class SleepAverageResponse {
    private String averageSleepTime;
    private Map<String, Long> feelingCount;

    public SleepAverageResponse() {
    }

    public SleepAverageResponse(String averageSleepTime, Map<String, Long> feelingCount) {
        this.averageSleepTime = averageSleepTime;
        this.feelingCount = feelingCount;
    }

    public String getAverageSleepTime() {
        return averageSleepTime;
    }

    public void setAverageSleepTime(String averageSleepTime) {
        this.averageSleepTime = averageSleepTime;
    }

    public Map<String, Long> getFeelingCount() {
        return feelingCount;
    }

    public void setFeelingCount(Map<String, Long> feelingCount) {
        this.feelingCount = feelingCount;
    }
}
