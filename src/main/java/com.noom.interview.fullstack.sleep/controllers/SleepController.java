package com.noom.interview.fullstack.sleep.controllers;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import com.noom.interview.fullstack.sleep.enums.Feeling;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.noom.interview.fullstack.sleep.repositories.SleepRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
public class SleepController {

    private final SleepRepository repository;

    public SleepController(SleepRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/sleeps")
    public ResponseEntity<List<Sleep>> getSleeps() {
        List<Sleep> sleeps = repository.findAll();
        return ResponseEntity.ok(sleeps);
    }

    @GetMapping("/sleeps/{id}")
    public ResponseEntity<Sleep> getSleepById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sleeps/user/{userId}")
    public ResponseEntity<List<Sleep>> getSleepsByUserId(@PathVariable UUID userId) {
        List<Sleep> sleeps = repository.findByUserId(userId);
        return ResponseEntity.ok(sleeps);
    }
}
