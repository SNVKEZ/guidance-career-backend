package org.ssu.belous.dto;

import org.ssu.belous.models.Questionnaire;

import java.util.UUID;

public record QuestionnaireDto(
        UUID id,
        String code,
        String title,
        String description,
        String imageUrl,
        int questionsCount,
        int timeLimitMinutes
) {
    public static QuestionnaireDto fromEntity(Questionnaire q) {
        return new QuestionnaireDto(
                q.getId(),
                q.getCode(),
                q.getTitle(),
                q.getDescription(),
                q.getImageUrl(),
                q.getQuestions().size(),
                q.getTimeLimitMinutes()
        );
    }
}

