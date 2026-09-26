package com.placementor.backend.repository;

import com.placementor.backend.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    Optional<Roadmap> findTopByUserIdOrderByIdDesc(Long userId);
    Optional<Roadmap> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
