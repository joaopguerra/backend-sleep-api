package com.noom.interview.fullstack.sleep.services;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.domain.User;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import com.noom.interview.fullstack.sleep.exceptions.IncorrectDateException;
import com.noom.interview.fullstack.sleep.exceptions.SleepException;
import com.noom.interview.fullstack.sleep.exceptions.UserException;
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
        if (sleep.isPresent()) {
            return Optional.of(SleepResponseDTO.toSleepDto(sleep.get()));
        } else {
            throw new SleepException("Sleep not found");
        }
    }

    public List<Sleep> getSleepsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        return sleepRepository.findByUserId(user.getId());
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

}
