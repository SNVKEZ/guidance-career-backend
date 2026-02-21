package org.ssu.belous.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.dto.*;
import org.ssu.belous.models.AnswerOption;
import org.ssu.belous.models.Dimension;
import org.ssu.belous.models.Questionnaire;
import org.ssu.belous.repos.AnswerOptionRepository;
import org.ssu.belous.repos.QuestionnaireRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestAnalysisService {

    private final QuestionnaireRepository questionnaireRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public TestAnalysisResult analyze(UUID questionnaireId,
                                      Map<UUID, UUID> questionToSelectedOptionId,
                                      String preferredDimension) {

        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        List<AnswerOption> selectedOptions =
                answerOptionRepository.findAllById(questionToSelectedOptionId.values());
        Map<Dimension, Long> dimensionToCount = selectedOptions.stream()
                .collect(Collectors.groupingBy(AnswerOption::getDimension, Collectors.counting()));

        List<DimensionResult> dimensionResults = dimensionToCount.entrySet().stream()
                .map(entry -> {
                    Dimension dim = entry.getKey();

                    List<FacultyDto> faculties = dim.getFaculties().stream()
                            .map(f -> new FacultyDto(
                                    f.getId(),
                                    f.getName(),
                                    f.getLink(),
                                    f.getDirections().stream()
                                            .map(d -> new DirectionDto(d.getId(), d.getName()))
                                            .toList()
                            ))
                            .toList();

                    List<DirectionDto> directions = dim.getDirections().stream()
                            .map(d -> new DirectionDto(d.getId(), d.getName()))
                            .toList();

                    return new DimensionResult(
                            dim.getName(),
                            entry.getValue().intValue(),
                            dim.getDescription(),
                            faculties,
                            directions
                    );
                })
                .sorted(Comparator.comparingInt(DimensionResult::selectedCount).reversed())
                .collect(Collectors.toList());

        if (preferredDimension != null) {
            Optional<DimensionResult> preferred = dimensionResults.stream()
                    .filter(d -> d.dimensionName().equals(preferredDimension))
                    .findFirst();

            if (preferred.isPresent()) {
                dimensionResults.remove(preferred.get());
                dimensionResults.add(0, preferred.get());
            }
        }

        List<QuestionAnswerResult> questionAnswers =
                questionnaire.getQuestions().stream()
                        .map(q -> {
                            UUID selectedOptionId = questionToSelectedOptionId.get(q.getId());
                            if (selectedOptionId == null) return null;

                            AnswerOption option = selectedOptions.stream()
                                    .filter(o -> o.getId().equals(selectedOptionId))
                                    .findFirst()
                                    .orElse(null);

                            if (option == null) return null;

                            return new QuestionAnswerResult(
                                    q.getId(),
                                    q.getText(),
                                    option.getText()
                            );
                        })
                        .filter(Objects::nonNull)
                        .toList();

        return new TestAnalysisResult(dimensionResults, questionAnswers);
    }

}