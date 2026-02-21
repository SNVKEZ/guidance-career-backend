package org.ssu.belous.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssu.belous.dto.DimensionRelationsDto;
import org.ssu.belous.services.DimensionRelationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/dimension-relations")
@RequiredArgsConstructor
public class DimensionRelationController {
    private static final Logger log = LoggerFactory.getLogger(DimensionRelationController.class);

    private final DimensionRelationService service;

    @GetMapping("/{dimensionId}/relations")
    public DimensionRelationsDto getRelations(@PathVariable("dimensionId") UUID dimensionId) {
        return service.getRelations(dimensionId);
    }

    @PutMapping("/update/{dimensionId}")
    public ResponseEntity<Void> updateRelations(@PathVariable("dimensionId") UUID dimensionId,
                                                @RequestBody DimensionRelationsDto dto) {
        service.updateRelations(
                new DimensionRelationsDto(
                        dimensionId,
                        dto.facultyIds(),
                        dto.directionIds()
                )
        );
        return ResponseEntity.ok().build();
    }
}

