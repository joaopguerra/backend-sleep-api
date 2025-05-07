package com.noom.interview.fullstack.sleep.services;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import com.noom.interview.fullstack.sleep.repositories.SleepRepository;
import com.noom.interview.fullstack.sleep.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
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
        Optional<SleepResponseDTO> sleepDTO = sleep.map(SleepResponseDTO::toSleepDto);
        if (sleepDTO.isPresent()) {
            return sleepDTO;
        } else {
            throw new RuntimeException("Sleep not found");
        }
    }

    public List<Sleep> getSleepsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sleepRepository.findByUserId(user.getId());
    }

    public Sleep createSleep(SleepRequestDTO sleepRequestDTO) {
        Sleep sleep = new Sleep();
        User user = userRepository.findById(UUID.fromString(sleepRequestDTO.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user != null) {
            sleep.setUser(user);
            sleep.setSleepDate(sleepRequestDTO.getSleepDate());
            sleep.setTimeInBed(sleepRequestDTO.getWakeTime().minus(Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime())));
            sleep.setTotalTimeInBed(sleepRequestDTO.getBedTime().plus(Duration.between(sleepRequestDTO.getBedTime(), sleepRequestDTO.getWakeTime())));
            sleep.setFeeling(Feeling.fromString(sleepRequestDTO.getFeeling()));
            sleepRepository.save(sleep);
        }
        return sleep;
    }
}
