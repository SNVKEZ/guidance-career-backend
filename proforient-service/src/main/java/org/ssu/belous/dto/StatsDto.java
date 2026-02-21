package org.ssu.belous.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StatsDto(
        String result,
        LocalDateTime completedAt,
        UUID testId
) {}