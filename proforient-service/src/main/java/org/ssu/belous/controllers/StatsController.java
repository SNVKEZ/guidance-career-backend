package org.ssu.belous.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssu.belous.dto.DimensionDto;
import org.ssu.belous.dto.StatsDto;
import org.ssu.belous.services.FacultyService;
import org.ssu.belous.services.QuestionnaireService;
import org.ssu.belous.services.StatsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private static final Logger log = LoggerFactory.getLogger(StatsController.class);

    private final StatsService statsService;
    private final QuestionnaireService questionnaireService;

    @GetMapping
    public List<StatsDto> getAllStats() {
        log.info("Получение всех статистических данных");
        return statsService.getAll();
    }

    @GetMapping("/{testId}")
    public List<StatsDto> getStatsByTestId(@PathVariable("testId") UUID testId) {
        log.info("Получение статистики по testId: {}", testId);
        return statsService.getByTestId(testId);
    }

    @GetMapping("/{id}/dimensions")
    public List<DimensionDto> getDimensions(@PathVariable("id") UUID id) {
        return questionnaireService.getDimensionsByQuestionnaireId(id);
    }
}
