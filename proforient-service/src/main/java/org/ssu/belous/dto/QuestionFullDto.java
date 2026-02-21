package org.ssu.belous.dto;

import java.util.List;
import java.util.UUID;

public record QuestionFullDto(
        UUID id,
        int number,
        String text,
        List<AnswerOptionDto> answers
) {}
