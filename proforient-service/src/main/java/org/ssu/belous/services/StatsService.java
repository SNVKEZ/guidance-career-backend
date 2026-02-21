package org.ssu.belous.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.belous.dto.DimensionResult;
import org.ssu.belous.dto.StatsDto;
import org.ssu.belous.dto.TestAnalysisResult;
import org.ssu.belous.models.Stats;
import org.ssu.belous.repos.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    @Transactional(readOnly = true)
    public List<StatsDto> getAll() {
        return statsRepository.findAll()
                .stream()
                .map(s -> new StatsDto(
                        s.getResult(),
                        s.getCompletedAt(),
                        s.getTestId()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatsDto> getByTestId(UUID testId) {
        return statsRepository.findByTestId(testId)
                .stream()
                .map(s -> new StatsDto(
                        s.getResult(),
                        s.getCompletedAt(),
                        s.getTestId()
                ))
                .toList();
    }


    @Transactional
    public void saveStats(UUID questionnaireId, TestAnalysisResult analysisResult) {

        List<DimensionResult> dimensions = analysisResult.dimensions();

        if (dimensions == null || dimensions.isEmpty()) {
            return;
        }

        DimensionResult topDimension = dimensions.get(0);

        Stats stats = new Stats();
        stats.setTestId(questionnaireId);
        stats.setResult(topDimension.dimensionName()); // ✅ правильное поле
        stats.setCompletedAt(LocalDateTime.now());

        statsRepository.save(stats);
    }

    @Transactional
    public int deleteOlderThanSixMonths() {
        LocalDateTime threshold =
                LocalDateTime.now().minusMonths(6);

        return statsRepository.deleteOlderThan(threshold);
    }
}
