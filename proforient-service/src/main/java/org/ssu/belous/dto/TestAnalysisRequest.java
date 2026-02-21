package org.ssu.belous.dto;

import java.util.Map;
import java.util.UUID;

public record TestAnalysisRequest(
        String questionnaireCode,
        Map<UUID, UUID> answers,
        String preferredDimension
) {}