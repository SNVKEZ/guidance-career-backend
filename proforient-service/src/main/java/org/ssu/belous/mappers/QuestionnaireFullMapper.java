package org.ssu.belous.mappers;

import org.springframework.stereotype.Component;
import org.ssu.belous.dto.AnswerOptionDto;
import org.ssu.belous.dto.DimensionDto;
import org.ssu.belous.dto.QuestionFullDto;
import org.ssu.belous.dto.QuestionnaireFullDto;
import org.ssu.belous.models.AnswerOption;
import org.ssu.belous.models.Dimension;
import org.ssu.belous.models.Question;
import org.ssu.belous.models.Questionnaire;

@Component
public class QuestionnaireFullMapper {

    public QuestionnaireFullDto toDto(Questionnaire q) {
        return new QuestionnaireFullDto(
                q.getId(),
                q.getCode(),
                q.getTitle(),
                q.getDescription(),
                q.getImageUrl(),
                q.getTimeLimitMinutes(),
                q.getDimensions().stream()
                        .map(this::toDimensionDto)
                        .toList(),
                q.getQuestions().stream()
                        .map(this::toQuestionDto)
                        .toList()
        );
    }

    private DimensionDto toDimensionDto(Dimension d) {
        return new DimensionDto(
                d.getId(),
                d.getName(),
                d.getDescription()
        );
    }

    private QuestionFullDto toQuestionDto(Question question) {
        return new QuestionFullDto(
                question.getId(),
                question.getNumber(),
                question.getText(),
                question.getOptions().stream()
                        .map(this::toOptionDto)
                        .toList()
        );
    }

    private AnswerOptionDto toOptionDto(AnswerOption a) {
        return new AnswerOptionDto(
                a.getId(),
                a.getLabel(),
                a.getText(),
                a.getDimension().getId()
        );
    }
}
