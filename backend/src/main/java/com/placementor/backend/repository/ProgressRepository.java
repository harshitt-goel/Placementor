package com.placementor.backend.repository;

import com.placementor.backend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndTaskName(Long userId, String taskName);
    List<Progress> findByUserIdAndCompletedTrue(Long userId);
    long countByUserIdAndCompletedTrue(Long userId);
    void deleteByUserId(Long userId);
}
