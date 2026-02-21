package org.ssu.belous.dto;

import java.util.UUID;

public record AnswerOptionDto(
        UUID id,
        String label,
        String text,
        UUID dimensionId
) {}
