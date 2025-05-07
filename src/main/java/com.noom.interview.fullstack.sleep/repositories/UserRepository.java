package com.noom.interview.fullstack.sleep.repositories;

import com.noom.interview.fullstack.sleep.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
