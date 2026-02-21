package org.ssu.belous.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.dto.DimensionRelationsDto;
import org.ssu.belous.models.Dimension;
import org.ssu.belous.models.Direction;
import org.ssu.belous.models.Faculty;
import org.ssu.belous.repos.DimensionRepository;
import org.ssu.belous.repos.DirectionRepository;
import org.ssu.belous.repos.FacultyRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DimensionRelationService {

    private final DimensionRepository dimensionRepo;
    private final FacultyRepository facultyRepo;
    private final DirectionRepository directionRepo;

    public DimensionRelationsDto getRelations(UUID dimensionId) {
        Dimension d = dimensionRepo.findById(dimensionId)
                .orElseThrow(() -> new RuntimeException("Dimension not found"));

        return new DimensionRelationsDto(
                d.getId(),
                d.getFaculties().stream().map(Faculty::getId).toList(),
                d.getDirections().stream().map(Direction::getId).toList()
        );
    }

    public void updateRelations(DimensionRelationsDto dto) {
        Dimension d = dimensionRepo.findById(dto.dimensionId())
                .orElseThrow(() -> new RuntimeException("Dimension not found"));

        List<Faculty> faculties = dto.facultyIds() == null
                ? List.of()
                : facultyRepo.findAllById(dto.facultyIds());

        List<Direction> directions = dto.directionIds() == null
                ? List.of()
                : directionRepo.findAllById(dto.directionIds());

        d.setFaculties(faculties);
        d.setDirections(directions);
    }

}
