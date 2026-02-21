package org.ssu.belous.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssu.belous.models.Direction;

import java.util.UUID;

public interface DirectionRepository extends JpaRepository<Direction, UUID> {}
