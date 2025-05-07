package com.noom.interview.fullstack.sleep.services;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepDTO;
import com.noom.interview.fullstack.sleep.repositories.SleepRepository;
import com.noom.interview.fullstack.sleep.repositories.UserRepository;
import org.springframework.stereotype.Service;

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

    public List<SleepDTO> getAllSleeps() {
        return sleepRepository.findAll()
                .stream()
                .map(SleepDTO::toSleepDto)
                .collect(Collectors.toList());
    }

    public Optional<SleepDTO> getSleepById(UUID id) {
        Optional<Sleep> sleep = sleepRepository.findById(id);
        Optional<SleepDTO> sleepDTO = sleep.map(SleepDTO::toSleepDto);
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

    public Sleep createSleep(Sleep requestedSleep) {
        Sleep sleep = new Sleep();
        User user = userRepository.findById(requestedSleep.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user != null) {
            sleep.setSleepDate(requestedSleep.getSleepDate());
            sleep.setTimeInBed(requestedSleep.getTimeInBed());
            sleep.setTotalTimeInBed(requestedSleep.getTotalTimeInBed());
            sleep.setFeeling(requestedSleep.getFeeling());
            sleep.setUser(user);
            sleep = sleepRepository.save(sleep);
        }
        return sleep;
    }
}
