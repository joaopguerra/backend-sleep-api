package com.noom.interview.fullstack.sleep.controllers;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.dto.SleepDTO;
import com.noom.interview.fullstack.sleep.services.SleepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
public class SleepController {

    private final SleepService sleepService;

    public SleepController(SleepService sleepService) {
        this.sleepService = sleepService;
    }

    @GetMapping("/sleeps")
    public ResponseEntity<List<SleepDTO>> getSleeps() {
        List<SleepDTO> sleeps = sleepService.getAllSleeps();
        return ResponseEntity.status(HttpStatus.OK).body(sleeps);
    }

    @GetMapping("/sleeps/{id}")
    public ResponseEntity<SleepDTO> getSleepById(@PathVariable UUID id) {
        SleepDTO sleep = sleepService.getSleepById(id)
                .orElseThrow(() -> new RuntimeException("Sleep not found"));
        return ResponseEntity.status(HttpStatus.OK).body(sleep);
    }

    @GetMapping("/sleeps/user/{userId}")
    public ResponseEntity<List<Sleep>> getSleepsByUserId(@PathVariable UUID userId) {
        List<Sleep> sleeps = sleepService.getSleepsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(sleeps);
    }

    @PostMapping("/sleeps")
    public ResponseEntity<Sleep> createSleep(@RequestBody Sleep sleep) {
        Sleep createdSleep = sleepService.createSleep(sleep);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSleep);
    }
}
