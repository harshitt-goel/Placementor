package com.placementor.backend.repository;

import com.placementor.backend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findTopByUserIdOrderByIdDesc(Long userId);
    Optional<Resume> findByUserId(Long userId);
}
