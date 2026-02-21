package org.ssu.belous.dto;

import java.util.List;

public record TestAnalysisResult(
        List<DimensionResult> dimensions,
        List<QuestionAnswerResult> questionAnswers
) {}
