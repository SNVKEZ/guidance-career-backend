package org.ssu.belous.dto;

import java.util.List;



public record DimensionResult(
        String dimensionName,
        int selectedCount,
        String description,
        List<FacultyDto> faculties,
        List<DirectionDto> directions
) {}