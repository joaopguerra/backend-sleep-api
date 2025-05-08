package com.noom.interview.fullstack.sleep.repositories;

import com.noom.interview.fullstack.sleep.domain.Sleep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SleepRepository extends JpaRepository<Sleep, UUID> {
    List<Sleep> findByUserId(UUID userId);

    Optional<Sleep> findByIdAndIsDeleted(UUID id, boolean isDeleted);

    List<Sleep> findByUserIdAndSleepDateBetweenAndIsDeleted(UUID userId, LocalDateTime startDate, LocalDateTime endDate,
                                                            boolean isDeleted);
}
