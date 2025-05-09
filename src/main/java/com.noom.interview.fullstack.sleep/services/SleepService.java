package com.noom.interview.fullstack.sleep.services;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepAverageResponse;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import com.noom.interview.fullstack.sleep.exceptions.IncorrectDateException;
import com.noom.interview.fullstack.sleep.exceptions.SleepException;
import com.noom.interview.fullstack.sleep.exceptions.UserException;
import com.noom.interview.fullstack.sleep.repositories.SleepRepository;
import com.noom.interview.fullstack.sleep.repositories.UserRepository;
import com.noom.interview.fullstack.sleep.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SleepService {

    private static final Logger log = LoggerFactory.getLogger(SleepService.class);
    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;

    public SleepService(SleepRepository sleepRepository, UserRepository userRepository) {
        this.sleepRepository = sleepRepository;
        this.userRepository = userRepository;
    }

    public List<SleepResponseDTO> getAllSleeps() {
        log.info("Fetching all sleeps");
        return sleepRepository.findAll()
                .stream()
                .map(SleepResponseDTO::toSleepDto)
                .collect(Collectors.toList());
    }

    public Optional<SleepResponseDTO> getSleepById(UUID id) {
        log.info("Fetching sleep by ID: {}", id);
        Optional<Sleep> sleep = sleepRepository.findById(id);
        if (sleep.isPresent()) {
            return Optional.of(SleepResponseDTO.toSleepDto(sleep.get()));
        } else {
            log.error("Sleep not found with ID: {}", id);
            throw new SleepException("Sleep not found");
        }
    }

    public List<SleepResponseDTO> getSleepsByUserId(UUID userId) {
        log.info("Fetching sleeps for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserException("User not found");
                });
        List<Sleep> sleep = sleepRepository.findByUserId(user.getId());
        return sleep.stream().map(SleepResponseDTO::toSleepDto).collect(Collectors.toList());
    }

    public Sleep createSleep(SleepRequestDTO sleepRequestDTO) {
        log.info("Creating sleep entry for user ID: {}", sleepRequestDTO.getUserId());
        validateSleepTimes(sleepRequestDTO);

        Sleep sleep = new Sleep();
        User user = userRepository.findById(UUID.fromString(sleepRequestDTO.getUserId()))
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", sleepRequestDTO.getUserId());
                    return new UserException("User not found");
                });

        if (user != null) {
            sleep.setUser(user);
            sleep.setSleepDate(sleepRequestDTO.getSleepDate());

            Duration duration = Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime());
            sleep.setTimeInBed(sleepRequestDTO.getWakeTime().minus(duration));
            sleep.setTotalTimeInBed(sleepRequestDTO.getBedTime().plus(duration));

            sleep.setFeeling(Feeling.fromString(sleepRequestDTO.getFeeling()));
            sleepRepository.save(sleep);
            log.info("Sleep entry created successfully for user ID: {}", sleepRequestDTO.getUserId());
        }

        return sleep;
    }

    private void validateSleepTimes(SleepRequestDTO sleepRequestDTO) {
        log.info("Validating sleep times for user ID: {}", sleepRequestDTO.getUserId());
        if (sleepRequestDTO.getBedTime().isBefore(sleepRequestDTO.getSleepDate())) {
            log.error("Bed time {} cannot be before sleep date time {}", sleepRequestDTO.getBedTime(), sleepRequestDTO.getSleepDate());
            throw new IncorrectDateException("Bed time cannot be before Sleep date time");
        }

        if (sleepRequestDTO.getWakeTime().isBefore(sleepRequestDTO.getBedTime())) {
            log.error("Wake time {} cannot be before bed time {}", sleepRequestDTO.getWakeTime(), sleepRequestDTO.getBedTime());
            throw new IncorrectDateException("Wake time cannot be before bed time");
        }
    }

    public void deleteSleep(UUID id) {
        log.info("Deleting sleep entry with ID: {}", id);
        sleepRepository.findByIdAndIsDeleted(id, false)
                .ifPresentOrElse(sleep -> {
                    sleep.setDeleted(true);
                    sleepRepository.save(sleep);
                }, () -> {
                    log.error("Sleep not found with ID: {}", id);
                    throw new SleepException("Sleep not found");
                });
    }

    public SleepAverageResponse getAverageSleep(String startDate, String endDate, UUID userId) {
        log.info("Calculating average sleep for user ID: {} from {} to {}", userId, startDate, endDate);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserException("User not found");
                });

        SleepAverageResponse response = new SleepAverageResponse();
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime start = LocalDate.parse(startDate, parser).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, parser).atStartOfDay().plusDays(1).minusSeconds(1);

        if (start.isAfter(end)) {
            log.error("Start date {} cannot be after end date {}", start, end);
            throw new IncorrectDateException("Start date cannot be after end date");
        }

        List<Sleep> sleeps = sleepRepository.findByUserIdAndSleepDateBetweenAndIsDeleted(userId, start, end, false);

        double averageSleepTime = sleeps.stream()
                .mapToDouble(sleep -> {
                    LocalDateTime timeInBed = sleep.getTimeInBed();
                    LocalDateTime totalTimeInBed = sleep.getTotalTimeInBed();

                    return Duration.between(timeInBed, totalTimeInBed).toMinutes();
                })
                .average()
                .orElse(0.0);

        long averageMinutes = Math.round(averageSleepTime);
        LocalDateTime fakeStart = LocalDateTime.of(0, 1, 1, 0, 0);
        LocalDateTime fakeEnd = fakeStart.plusMinutes(averageMinutes);
        response.setAverageSleepTime(SleepUtils.formatDuration(fakeStart, fakeEnd));

        Map<String, Long> countingFeelings = sleeps.stream()
                .collect(Collectors.groupingBy(
                        sleep -> sleep.getFeeling().getName(),
                        Collectors.counting()
                ));
        response.setFeelingCount(countingFeelings);

        return response;
    }

}
