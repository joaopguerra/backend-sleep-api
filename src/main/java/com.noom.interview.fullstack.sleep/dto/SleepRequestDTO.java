package com.noom.interview.fullstack.sleep.dto;

import java.time.LocalDateTime;

public class SleepRequestDTO {
    private LocalDateTime sleepDate;
    private LocalDateTime bedTime;
    private LocalDateTime wakeTime;
    private String feeling;
    private String userId;

    public LocalDateTime getSleepDate() {
        return sleepDate;
    }

    public void setSleepDate(LocalDateTime sleepDate) {
        this.sleepDate = sleepDate;
    }

    public LocalDateTime getBedTime() {
        return bedTime;
    }

    public void setBedTime(LocalDateTime bedTime) {
        this.bedTime = bedTime;
    }

    public LocalDateTime getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(LocalDateTime wakeTime) {
        this.wakeTime = wakeTime;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}