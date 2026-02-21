package org.ssu.belous.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssu.belous.dto.TestAnalysisRequest;
import org.ssu.belous.dto.TestAnalysisResult;
import org.ssu.belous.models.Questionnaire;
import org.ssu.belous.services.PdfReportService;
import org.ssu.belous.services.QuestionnaireService;
import org.ssu.belous.services.StatsService;
import org.ssu.belous.services.TestAnalysisService;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalyzeController {
    private static final Logger log = LoggerFactory.getLogger(AnalyzeController.class);

    private final QuestionnaireService questionnaireService;
    private final PdfReportService pdfReportService;
    private final TestAnalysisService testAnalysisService;
    private final StatsService statsService;

    @PostMapping()
    public TestAnalysisResult analyzeTest(@RequestBody TestAnalysisRequest request) {

        Questionnaire questionnaire =
                questionnaireService.getFull(request.questionnaireCode());

        TestAnalysisResult result =
                testAnalysisService.analyze(
                        questionnaire.getId(),
                        request.answers(),
                        request.preferredDimension()
                );

        boolean hasTie = result.dimensions().size() > 1 &&
                result.dimensions().get(0).selectedCount()
                        == result.dimensions().get(1).selectedCount();

        if (!hasTie || request.preferredDimension() != null) {
            statsService.saveStats(questionnaire.getId(), result);
        }

        log.info("Анализ для ответов на тест {} произведен", request.questionnaireCode());
        return result;
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestBody TestAnalysisRequest request) {

        Questionnaire questionnaire =
                questionnaireService.getFull(request.questionnaireCode());

        TestAnalysisResult result =
                testAnalysisService.analyze(
                        questionnaire.getId(),
                        request.answers(),
                        request.preferredDimension()
                );

        byte[] pdf = pdfReportService.generate(result, questionnaire.getTitle());

        log.info("Выгрузка ответов в пдф-формате на тест {} произведен", request.questionnaireCode());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"result.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
