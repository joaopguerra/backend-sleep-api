package com.noom.interview.fullstack.sleep.repositories;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SleepRepository extends JpaRepository<Sleep, UUID> {
    List<Sleep> findByUserId(UUID userId);
}
