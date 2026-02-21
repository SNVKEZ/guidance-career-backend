package org.ssu.belous.dto;

import java.util.List;
import java.util.UUID;

public record DimensionRelationsDto(
        UUID dimensionId,
        List<UUID> facultyIds,
        List<UUID> directionIds
) {}
