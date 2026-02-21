package org.ssu.belous.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ssu.belous.services.StatsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsCleanupScheduler {

    private final StatsService statsService;

    @Scheduled(cron = "${app.stats.cleanup-cron}")
    public void cleanupOldStats() {

        int deleted = statsService.deleteOlderThanSixMonths();

        if (deleted > 0) {
            log.info("Автоочистка статистики: удалено {} записей", deleted);
        } else {
            log.debug("Автоочистка статистики: старых записей нет");
        }
    }
}
