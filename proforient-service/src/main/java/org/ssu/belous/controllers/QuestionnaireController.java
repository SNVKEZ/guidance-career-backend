package org.ssu.belous.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ssu.belous.dto.*;
import org.ssu.belous.services.*;
import org.ssu.belous.mappers.FacultyMapper;
import org.ssu.belous.mappers.QuestionnaireFullMapper;
import org.ssu.belous.models.Questionnaire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController {
    private static final Logger log = LoggerFactory.getLogger(QuestionnaireController.class);

    private final QuestionnaireService questionnaireService;
    private final QuestionnaireFullMapper questionnaireFullMapper;
    private final SguDirectionsParserService parserService;
    private final FacultyService facultyService;
    private final DimensionRelationService service;

    @GetMapping
    public List<QuestionnaireDto> getQuestionnaires() {
        log.info("Получен полный список тестов");
        return questionnaireService.getAll();
    }

    @GetMapping("/{code}")
    public QuestionnaireFullDto getFull(@PathVariable("code") String code) {
        Questionnaire q = questionnaireService.getFull(code);
        log.info("Получен тест по коду: {}", code);
        return questionnaireFullMapper.toDto(q);
    }


    @GetMapping("/{id}/relations")
    public DimensionRelationsDto get(@PathVariable UUID id) {
        return service.getRelations(id);
    }

    @PutMapping("/{id}/relations")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody DimensionRelationsDto dto) {
        service.updateRelations(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> create(@RequestPart("data") QuestionnaireCreateDto dto,
                                    @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Questionnaire questionnaire = questionnaireService.create(dto, image);
            return ResponseEntity.ok(questionnaireFullMapper.toDto(questionnaire));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("error" + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{code}")
    public ResponseEntity<Void> delete(@PathVariable("code") String code) {
        questionnaireService.deleteByCode(code);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/faculties")
    public List<FacultyDto> getFacultiesWithDirections() {
        return FacultyMapper.toDtoList(
                parserService.parseFacultiesWithDirectionsAndLinks()
        );
    }

    @GetMapping("/facultiesDB")
    public List<FacultyDto> getFacultiesFromDB() {
        return FacultyMapper.toDtoList(
                facultyService.getAllFacultiesWithDirections()
        );
    }

    @PostMapping("/faculties/update")
    public ResponseEntity<String> updateFaculties() {
        try {
            facultyService.updateFacultiesFromParser();
            return ResponseEntity.ok("Данные факультетов успешно обновлены из источника.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка при обновлении: " + e.getMessage());
        }
    }

    @PutMapping(value = "/update/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> update(
            @PathVariable("code") String code,
            @RequestPart("data") QuestionnaireCreateDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Questionnaire questionnaire = questionnaireService.update(code, dto, image);
            return ResponseEntity.ok(questionnaireFullMapper.toDto(questionnaire));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/faculties/update-if-changed")
    public ResponseEntity<String> updateIfChanged() {
        facultyService.updateFacultiesIfChanged();
        return ResponseEntity.ok("Проверка и обновление завершены.");
    }

    @PostMapping("/faculties/check")
    public ResponseEntity<String> checkData() {
        boolean upToDate = facultyService.isDataUpToDate();
        return ResponseEntity.ok(upToDate ? "Данные актуальны." : "Данные устарели — требуется обновление.");
    }

    @PostMapping("/faculties/update-force")
    @Transactional
    public ResponseEntity<String> forceUpdateFaculties() {
        try {
            facultyService.updateFacultiesFromParser();
            return ResponseEntity.ok("Данные факультетов принудительно обновлены из источника СГУ.");
        } catch (Exception e) {
            log.error("Ошибка при принудительном обновлении факультетов", e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при обновлении: " + e.getMessage());
        }
    }

    @PostMapping("/faculties/check-and-update")
    public ResponseEntity<Map<String, Object>> checkAndUpdate() {
        boolean upToDate = facultyService.isDataUpToDate();
        Map<String, Object> response = new HashMap<>();
        response.put("upToDate", upToDate);

        if (!upToDate) {
            facultyService.updateFacultiesFromParser();
            response.put("message", "Данные были устаревшими — выполнено обновление.");
            response.put("updated", true);
        } else {
            response.put("message", "Данные актуальны — обновление не требуется.");
            response.put("updated", false);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/faculties/check-status")
    public ResponseEntity<Map<String, Object>> checkStatus() {
        boolean upToDate = facultyService.isDataUpToDate();
        Map<String, Object> response = new HashMap<>();
        response.put("upToDate", upToDate);
        response.put("message", upToDate ? "Данные актуальны" : "Данные устарели");
        return ResponseEntity.ok(response);
    }
}