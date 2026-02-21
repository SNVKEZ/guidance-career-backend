package org.ssu.belous.dto;

import java.util.List;
import java.util.UUID;

public record QuestionnaireFullDto(
        UUID id,
        String code,
        String title,
        String description,
        String imageUrl,
        int timeLimitMinutes,
        List<DimensionDto> dimensions,
        List<QuestionFullDto> questions
) {}
