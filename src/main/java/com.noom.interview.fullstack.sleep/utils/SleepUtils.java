package com.noom.interview.fullstack.sleep.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SleepUtils {
    public static String formatSleepDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM, d", Locale.US);
        return date.format(formatter);
    }

    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %02d min", hours, minutes);
    }

    public static String formatBedToWakeTime(LocalDateTime bed, LocalDateTime wake) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma", Locale.US);
        return bed.format(formatter).toLowerCase() + " - " + wake.format(formatter).toLowerCase();
    }

    public static String formatFeeling(String feeling) {
        return feeling.substring(0, 1).toUpperCase() + feeling.substring(1).toLowerCase();
    }
}
