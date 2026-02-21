package org.ssu.belous.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.models.Direction;
import org.ssu.belous.models.Faculty;
import org.ssu.belous.repos.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final SguDirectionsParserService parserService;

    public List<Faculty> getAllFacultiesWithDirections() {
        List<Faculty> faculties = facultyRepository.findAllWithDirections();
        faculties.sort(Comparator.comparing(Faculty::getName, String.CASE_INSENSITIVE_ORDER));
        faculties.forEach(faculty -> {
            faculty.getDirections().sort(Comparator.comparing(dir ->
                    extractCodeFromDirectionName(dir.getName())));
        });
        return faculties;
    }

    @Transactional
    public void updateFacultiesFromParser() {
        log.info("Запуск обновления данных факультетов из парсера...");

        try {
            List<Faculty> parsedFaculties = parserService.parseFacultiesWithDirectionsAndLinks();

            facultyRepository.deleteAllInBatch();
            log.info("Удалены все старые записи о факультетах и направлениях.");

            for (Faculty parsed : parsedFaculties) {
                Faculty facultyToSave = new Faculty();
                facultyToSave.setName(parsed.getName());
                facultyToSave.setLink(parsed.getLink());

                List<Direction> directionsToSave = new ArrayList<>();
                for (Direction parsedDir : parsed.getDirections()) {
                    Direction dir = new Direction();
                    dir.setName(parsedDir.getName());
                    dir.setFaculty(facultyToSave);
                    directionsToSave.add(dir);
                }

                facultyToSave.setDirections(directionsToSave);
                facultyRepository.save(facultyToSave);
            }

            log.info("Успешно обновлены данные: {} факультетов сохранено.", parsedFaculties.size());

        } catch (Exception e) {
            log.error("Ошибка при обновлении данных из парсера: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить данные факультетов", e);
        }
    }

    @Transactional
    public void updateFacultiesIfChanged() {
        if (!isDataUpToDate()) {
            updateFacultiesFromParser();
        } else {
            log.info("Данные актуальны — обновление не требуется.");
        }
    }

    public boolean isDataUpToDate() {
        try {
            List<Faculty> dbFaculties = getAllFacultiesWithDirections();
            Map<String, FacultyData> dbMap = mapToComparable(dbFaculties);

            List<Faculty> parsedFaculties = parserService.parseFacultiesWithDirectionsAndLinks();
            Map<String, FacultyData> parsedMap = mapToComparable(parsedFaculties);

            boolean isEqual = true;

            for (String name : dbMap.keySet()) {
                if (!parsedMap.containsKey(name)) {
                    log.warn("Факультет отсутствует в парсере: {}", name);
                    isEqual = false;
                }
            }

            for (String name : parsedMap.keySet()) {
                if (!dbMap.containsKey(name)) {
                    log.warn("Новый факультет в парсере: {}", name);
                    isEqual = false;
                    continue;
                }

                FacultyData db = dbMap.get(name);
                FacultyData parsed = parsedMap.get(name);

                if (!Objects.equals(db.link, parsed.link)) {
                    log.warn("Разная ссылка у факультета '{}': БД = {}, Парсер = {}", name, db.link, parsed.link);
                    isEqual = false;
                }

                Set<String> dbDirections = new HashSet<>(db.directions);
                Set<String> parsedDirections = new HashSet<>(parsed.directions);

                if (!dbDirections.equals(parsedDirections)) {
                    Set<String> added = new HashSet<>(parsedDirections);
                    added.removeAll(dbDirections);
                    Set<String> removed = new HashSet<>(dbDirections);
                    removed.removeAll(parsedDirections);

                    if (!added.isEmpty()) {
                        log.info("Новые направления у '{}': {}", name, added);
                    }
                    if (!removed.isEmpty()) {
                        log.info("Удалённые направления у '{}': {}", name, removed);
                    }
                    isEqual = false;
                }
            }

            if (isEqual) {
                log.info("Данные актуальны: база и парсер полностью совпадают.");
            } else {
                log.info("Обнаружены различия между базой и парсером.");
            }

            return isEqual;

        } catch (Exception e) {
            log.error("Ошибка при сравнении данных: {}", e.getMessage(), e);
            return false;
        }
    }

    private record FacultyData(String link, List<String> directions) {
    }

    private Map<String, FacultyData> mapToComparable(List<Faculty> faculties) {
        return faculties.stream()
                .collect(Collectors.toMap(
                        Faculty::getName,
                        f -> new FacultyData(
                                f.getLink(),
                                f.getDirections().stream()
                                        .map(Direction::getName)
                                        .sorted(Comparator.comparing(this::extractCodeFromDirectionName))
                                        .toList()
                        ),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    private String extractCodeFromDirectionName(String directionName) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2,3})").matcher(directionName);
        return m.find() ? m.group(1) : "999.99.99";
    }
}