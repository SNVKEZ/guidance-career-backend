package org.ssu.belous.dto;

import java.util.UUID;

public record QuestionAnswerResult(
        UUID questionId,
        String questionText,
        String answerText
) {}
