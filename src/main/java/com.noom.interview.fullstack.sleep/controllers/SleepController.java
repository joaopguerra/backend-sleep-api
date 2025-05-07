package com.noom.interview.fullstack.sleep.controllers;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.dto.SleepRequestDTO;
import com.noom.interview.fullstack.sleep.dto.SleepResponseDTO;
import com.noom.interview.fullstack.sleep.services.SleepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
public class SleepController {

    private final SleepService sleepService;

    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @GetMapping("/sleeps")
    public ResponseEntity<List<SleepResponseDTO>> getSleeps() {
        List<SleepResponseDTO> sleeps = sleepService.getAllSleeps();
        return ResponseEntity.status(HttpStatus.OK).body(sleeps);
    }

    @GetMapping("/sleeps/{id}")
    public ResponseEntity<SleepResponseDTO> getSleepById(@PathVariable UUID id) {
        SleepResponseDTO sleep = sleepService.getSleepById(id)
                .orElseThrow(() -> new RuntimeException("Sleep not found"));
        return ResponseEntity.status(HttpStatus.OK).body(sleep);
    }

    @GetMapping("/sleeps/user/{userId}")
    public ResponseEntity<List<Sleep>> getSleepsByUserId(@PathVariable UUID userId) {
        List<Sleep> sleeps = sleepService.getSleepsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(sleeps);
    }

    @PostMapping("/sleeps")
    public ResponseEntity<Sleep> createSleep(@RequestBody SleepRequestDTO sleepRequestDTO) {
        Sleep createdSleep = sleepService.createSleep(sleepRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSleep);
    }
}
