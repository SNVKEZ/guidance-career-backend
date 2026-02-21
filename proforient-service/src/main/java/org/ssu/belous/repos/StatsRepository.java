package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.models.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.query.Param;

public interface StatsRepository extends JpaRepository<Stats, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Stats s WHERE s.completedAt < :threshold")
    int deleteOlderThan(@Param("threshold") LocalDateTime threshold);

    @Transactional
    List<Stats> findByTestId(UUID testId);
}
