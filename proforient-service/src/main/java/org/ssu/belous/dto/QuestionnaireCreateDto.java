package org.ssu.belous.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionnaireCreateDto {
    private String code;
    private String slug;
    private String title;
    private String description;
    private Integer timeLimitMinutes;

    private List<DimensionDto> dimensions;
    private List<QuestionDto> questions;

    @Data
    public static class DimensionDto {
        private UUID id;
        private String name;
        private String description;
    }

    @Data
    public static class QuestionDto {
        private Integer number;
        private String text;
        private List<OptionDto> options;
    }

    @Data
    public static class OptionDto {
        private String label;
        private String text;
        private UUID dimensionId;
    }
}
