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

    private final SleepRepository sleepRepository;
    private final UserRepository userRepository;

    public SleepService(SleepRepository sleepRepository, UserRepository userRepository) {
        this.sleepRepository = sleepRepository;
        this.userRepository = userRepository;
    }

    public List<SleepResponseDTO> getAllSleeps() {
        return sleepRepository.findAll()
                .stream()
                .map(SleepResponseDTO::toSleepDto)
                .collect(Collectors.toList());
    }

    public Optional<SleepResponseDTO> getSleepById(UUID id) {
        Optional<Sleep> sleep = sleepRepository.findById(id);
        if (sleep.isPresent()) {
            return Optional.of(SleepResponseDTO.toSleepDto(sleep.get()));
        } else {
            throw new SleepException("Sleep not found");
        }
    }

    public List<SleepResponseDTO> getSleepsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        List<Sleep> sleep = sleepRepository.findByUserId(user.getId());
        return sleep.stream().map(SleepResponseDTO::toSleepDto).collect(Collectors.toList());
    }

    public Sleep createSleep(SleepRequestDTO sleepRequestDTO) {
        validateSleepTimes(sleepRequestDTO);

        Sleep sleep = new Sleep();
        User user = userRepository.findById(UUID.fromString(sleepRequestDTO.getUserId()))
                .orElseThrow(() -> new UserException("User not found"));

        if (user != null) {
            sleep.setUser(user);
            sleep.setSleepDate(sleepRequestDTO.getSleepDate());

            Duration duration = Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime());
            sleep.setTimeInBed(sleepRequestDTO.getWakeTime().minus(duration));
            sleep.setTotalTimeInBed(sleepRequestDTO.getBedTime().plus(duration));

            sleep.setFeeling(Feeling.fromString(sleepRequestDTO.getFeeling()));
            sleepRepository.save(sleep);
        }

        return sleep;
    }

    private void validateSleepTimes(SleepRequestDTO sleepRequestDTO) {
        if (sleepRequestDTO.getBedTime().isBefore(sleepRequestDTO.getSleepDate())) {
            throw new IncorrectDateException("Bed time cannot be before Sleep date time");
        }

        if (sleepRequestDTO.getWakeTime().isBefore(sleepRequestDTO.getBedTime())) {
            throw new IncorrectDateException("Wake time cannot be before bed time");
        }
    }

    public void deleteSleep(UUID id) {
        sleepRepository.findByIdAndIsDeleted(id, false)
                .ifPresentOrElse(sleep -> {
                    sleep.setDeleted(true);
                    sleepRepository.save(sleep);
                }, () -> {
                    throw new SleepException("Sleep not found");
                });
    }

    public SleepAverageResponse getAverageSleep(String startDate, String endDate, UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        SleepAverageResponse response = new SleepAverageResponse();
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime start = LocalDate.parse(startDate, parser).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, parser).atStartOfDay().plusDays(1).minusSeconds(1);

        if (start.isAfter(end)) {
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
