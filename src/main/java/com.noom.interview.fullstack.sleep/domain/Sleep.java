package com.noom.interview.fullstack.sleep.domain;

import com.noom.interview.fullstack.sleep.enums.Feeling;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Sleep {

    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime sleepDate;
    private LocalDateTime timeInBed;
    private LocalDateTime totalTimeInBed;

    @Enumerated(EnumType.STRING)
    private Feeling feeling;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isDeleted = false;

    public Sleep() {
    }

    public Sleep(UUID id, LocalDateTime sleepDate, LocalDateTime timeInBed, LocalDateTime totalTimeInBed, Feeling feeling, User user, boolean isDeleted) {
        this.id = id;
        this.sleepDate = sleepDate;
        this.timeInBed = timeInBed;
        this.totalTimeInBed = totalTimeInBed;
        this.feeling = feeling;
        this.user = user;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getSleepDate() {
        return sleepDate;
    }

    public void setSleepDate(LocalDateTime sleepDate) {
        this.sleepDate = sleepDate;
    }

    public LocalDateTime getTimeInBed() {
        return timeInBed;
    }

    public void setTimeInBed(LocalDateTime timeInBed) {
        this.timeInBed = timeInBed;
    }

    public LocalDateTime getTotalTimeInBed() {
        return totalTimeInBed;
    }

    public void setTotalTimeInBed(LocalDateTime totalTimeInBed) {
        this.totalTimeInBed = totalTimeInBed;
    }

    public Feeling getFeeling() {
        return feeling;
    }

    public void setFeeling(Feeling feeling) {
        this.feeling = feeling;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

