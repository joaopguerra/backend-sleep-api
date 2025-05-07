package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class SleepDTO {

    private final String sleepDate;
    private final String timeInBed;
    private final String totalTimeInBed;
    private final String feeling;

    public SleepDTO(String sleepDate, String timeInBed, String totalTimeInBed, String feeling) {
        this.sleepDate = sleepDate;
        this.timeInBed = timeInBed;
        this.totalTimeInBed = totalTimeInBed;
        this.feeling = feeling;
    }

    public static SleepDTO toSleepDto(Sleep sleep) {
        String sleepDate = formatSleepDate(sleep.getSleepDate());
        String timeInBed = formatDuration(sleep.getTimeInBed(), sleep.getTotalTimeInBed());
        String totalTimeInBed = formatBedToWakeTime(sleep.getTimeInBed(), sleep.getTotalTimeInBed());
        String feeling = formatFeeling(sleep.getFeeling().name());

        return new SleepDTO(sleepDate, timeInBed, totalTimeInBed, feeling);
    }


    private static String formatSleepDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, d", Locale.US);
        return date.format(formatter);
    }

    private static String formatDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %02d min", hours, minutes);
    }

    private static String formatBedToWakeTime(LocalDateTime bed, LocalDateTime wake) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma", Locale.US);
        return bed.format(formatter).toLowerCase() + " - " + wake.format(formatter).toLowerCase();
    }

    private static String formatFeeling(String feeling) {
        return feeling.substring(0, 1).toUpperCase() + feeling.substring(1).toLowerCase();
    }
}
