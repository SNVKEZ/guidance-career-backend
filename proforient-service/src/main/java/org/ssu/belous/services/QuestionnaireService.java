package org.ssu.belous.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.ssu.belous.dto.DimensionDto;
import org.ssu.belous.dto.QuestionnaireCreateDto;
import org.ssu.belous.dto.QuestionnaireDto;
import org.ssu.belous.models.AnswerOption;
import org.ssu.belous.models.Dimension;
import org.ssu.belous.models.Question;
import org.ssu.belous.models.Questionnaire;
import org.ssu.belous.repos.DimensionRepository;
import org.ssu.belous.repos.QuestionnaireRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final DimensionRepository dimensionRepository;



    @Transactional
    public List<QuestionnaireDto> getAll() {
        return questionnaireRepository.findAllWithQuestions()
                .stream()
                .map(QuestionnaireDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Questionnaire getFull(String code) {
        Questionnaire q = questionnaireRepository
                .findByCodeWithQuestions(code)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found"));

        Hibernate.initialize(q.getDimensions());
        q.getQuestions().forEach(question ->
                Hibernate.initialize(question.getOptions()));

        return q;
    }

    @Transactional
    public Questionnaire create(QuestionnaireCreateDto dto, MultipartFile image) {

        if (questionnaireRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Тест с кодом '" + dto.getCode() + "' уже существует");
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setCode(dto.getCode());
        questionnaire.setTitle(dto.getTitle());
        questionnaire.setDescription(dto.getDescription());
        questionnaire.setTimeLimitMinutes(dto.getTimeLimitMinutes());

        System.out.println("image = " + image);

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Загруженный файл не является изображением");
            }

            if (image.getSize() > 10 * 1024 * 1024) {
                throw new RuntimeException("Изображение слишком большое (максимум 10 МБ)");
            }

            String originalFilename = image.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            String filename = UUID.randomUUID() + extension;

            String userHome = System.getProperty("user.home");
            String uploadDir = userHome + "/questionnaire-uploads/images";

            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(filename);

            try {
                Files.createDirectories(uploadPath);

                image.transferTo(filePath.toFile());

                questionnaire.setImageUrl("/uploads/images/" + filename);
                System.out.println("Сохраняю изображение в: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Не удалось сохранить изображение: " + e.getMessage(), e);
            }
        }

        Map<UUID, Dimension> dimensionMap = new HashMap<>();

        for (QuestionnaireCreateDto.DimensionDto dDto : dto.getDimensions()) {
            if (dDto.getDescription() == null || dDto.getDescription().isBlank()) {
                throw new RuntimeException("Описание характеристики обязательно");
            }
            Dimension dimension = new Dimension();
            dimension.setName(dDto.getName());
            dimension.setDescription(dDto.getDescription());
            dimension.setQuestionnaire(questionnaire);

            questionnaire.getDimensions().add(dimension);
            dimensionMap.put(dDto.getId(), dimension);
        }

        for (QuestionnaireCreateDto.QuestionDto qDto : dto.getQuestions()) {
            Question question = new Question();
            question.setNumber(qDto.getNumber());
            question.setText(qDto.getText());
            question.setQuestionnaire(questionnaire);

            questionnaire.getQuestions().add(question);

            for (QuestionnaireCreateDto.OptionDto oDto : qDto.getOptions()) {
                AnswerOption option = new AnswerOption();
                option.setLabel(oDto.getLabel());
                option.setText(oDto.getText());
                option.setQuestion(question);

                Dimension dimension = dimensionMap.get(oDto.getDimensionId());
                if (dimension == null) {
                    throw new RuntimeException("Не найдена характеристика с ID: " + oDto.getDimensionId());
                }
                option.setDimension(dimension);

                question.getOptions().add(option);
            }
        }

        return questionnaireRepository.save(questionnaire);
    }

    @Transactional
    public void deleteByCode(String code) {
        if (!questionnaireRepository.existsByCode(code)) {
            throw new RuntimeException("Тест не найден");
        }
        questionnaireRepository.deleteByCode(code);
    }

    @Transactional
    public Questionnaire update(String code, QuestionnaireCreateDto dto, MultipartFile image) {
        Questionnaire questionnaire = questionnaireRepository
                .findByCode(code)
                .orElseThrow(() -> new RuntimeException("Тест с кодом '" + code + "' не найден"));

        Hibernate.initialize(questionnaire.getDimensions());
        Hibernate.initialize(questionnaire.getQuestions());
        for (Question q : questionnaire.getQuestions()) {
            Hibernate.initialize(q.getOptions());
        }

        for (Question question : questionnaire.getQuestions()) {
            question.getOptions().clear();
        }

        questionnaire.getDimensions().clear();
        questionnaire.getQuestions().clear();

        questionnaire.setTitle(dto.getTitle());
        questionnaire.setDescription(dto.getDescription());
        questionnaire.setTimeLimitMinutes(dto.getTimeLimitMinutes());

        Map<UUID, Dimension> dimensionMap = new HashMap<>();

        for (QuestionnaireCreateDto.DimensionDto dDto : dto.getDimensions()) {
            if (dDto.getDescription() == null || dDto.getDescription().isBlank()) {
                throw new RuntimeException("Описание характеристики обязательно");
            }
            Dimension dimension = new Dimension();
            dimension.setName(dDto.getName());
            dimension.setDescription(dDto.getDescription());
            dimension.setQuestionnaire(questionnaire);

            questionnaire.getDimensions().add(dimension);
            dimensionMap.put(dDto.getId(), dimension);
        }

        int questionNumber = 1;
        for (QuestionnaireCreateDto.QuestionDto qDto : dto.getQuestions()) {
            Question question = new Question();
            question.setNumber(questionNumber++);
            question.setText(qDto.getText());
            question.setQuestionnaire(questionnaire);

            int optionIndex = 0;
            for (QuestionnaireCreateDto.OptionDto oDto : qDto.getOptions()) {
                AnswerOption option = new AnswerOption();

                String label = oDto.getLabel();
                if (label == null || label.trim().isEmpty()) {
                    label = String.valueOf((char) ('A' + optionIndex));
                }
                option.setLabel(label);
                option.setText(oDto.getText());
                option.setQuestion(question);

                UUID tempDimId = oDto.getDimensionId();
                Dimension dimension = dimensionMap.get(tempDimId);
                if (dimension == null) {
                    throw new RuntimeException("Не найдена характеристика с временным ID: " + tempDimId);
                }
                option.setDimension(dimension);

                question.getOptions().add(option);
                optionIndex++;
            }

            questionnaire.getQuestions().add(question);
        }

        return questionnaireRepository.save(questionnaire);
    }

    @Transactional(readOnly = true)
    public List<DimensionDto> getDimensionsByQuestionnaireId(UUID questionnaireId) {

        return dimensionRepository
                .findAllByQuestionnaire_Id(questionnaireId)
                .stream()
                .map(d -> new DimensionDto(
                        d.getId(),
                        d.getName(),
                        d.getDescription()
                ))
                .toList();
    }
}
