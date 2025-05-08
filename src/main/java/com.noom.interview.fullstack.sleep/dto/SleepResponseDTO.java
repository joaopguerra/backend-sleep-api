package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import lombok.Data;

import static com.noom.interview.fullstack.sleep.utils.SleepUtils.*;

@Data
public class SleepResponseDTO {

    private String sleepId;
    private String sleepDate;
    private String timeInBed;
    private String totalTimeInBed;
    private String feeling;
    private String userId;
    private boolean isDeleted;

    public SleepResponseDTO() {
    }

    public SleepResponseDTO(String sleepId, String sleepDate, String timeInBed, String totalTimeInBed, String feeling, String userId, boolean isDeleted) {
        this.sleepId = sleepId;
        this.sleepDate = sleepDate;
        this.timeInBed = timeInBed;
        this.totalTimeInBed = totalTimeInBed;
        this.feeling = feeling;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public static SleepResponseDTO toSleepDto(Sleep sleep) {
        String sleepId = sleep.getId().toString();
        String sleepDate = formatSleepDate(sleep.getSleepDate());
        String timeInBed = formatDuration(sleep.getTimeInBed(), sleep.getTotalTimeInBed());
        String totalTimeInBed = formatBedToWakeTime(sleep.getTimeInBed(), sleep.getTotalTimeInBed());
        String feeling = formatFeeling(sleep.getFeeling().name());
        boolean isDeleted = sleep.isDeleted();

        return new SleepResponseDTO(sleepId, sleepDate, timeInBed, totalTimeInBed, feeling,
                sleep.getUser().getId().toString(), isDeleted);
    }
}
