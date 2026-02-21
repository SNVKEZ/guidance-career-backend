package org.ssu.belous.mappers;

import org.ssu.belous.dto.DirectionDto;
import org.ssu.belous.dto.FacultyDto;
import org.ssu.belous.models.Direction;
import org.ssu.belous.models.Faculty;

import java.util.List;

public class FacultyMapper {

    public static FacultyDto toDto(Faculty faculty) {
        return new FacultyDto(
                faculty.getId(),
                faculty.getName(),
                faculty.getLink(),
                faculty.getDirections()
                        .stream()
                        .map(FacultyMapper::toDto)
                        .toList()
        );
    }

    private static DirectionDto toDto(Direction direction) {
        return new DirectionDto(
                direction.getId(),
                direction.getName()
        );
    }

    public static List<FacultyDto> toDtoList(List<Faculty> faculties) {
        return faculties.stream()
                .map(FacultyMapper::toDto)
                .toList();
    }
}
