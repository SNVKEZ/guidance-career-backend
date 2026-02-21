package org.ssu.belous.dto;

import java.util.UUID;

public record DimensionDto(
        UUID id,
        String name,
        String description
) {}
