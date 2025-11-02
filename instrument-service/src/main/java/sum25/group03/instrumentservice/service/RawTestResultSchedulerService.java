package sum25.group03.instrumentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.service.RawTestResultService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawTestResultSchedulerService {

    private final RawTestResultService rawTestResultService;

    @Value("${raw-test-result.retention-days:30}")
    private int retentionDays;


    @Scheduled(cron = "0 0 2 * * ?")
    public void autoDeleteOldRawTestResults() {
        log.info("Starting scheduled auto-delete job for old raw test results (retention: {} days)", retentionDays);

        try {
            rawTestResultService.autoDeleteOldResults(retentionDays);
            log.info("Auto-delete job completed successfully");
        } catch (Exception e) {
            log.error("Error during auto-delete scheduled job: {}", e.getMessage(), e);
        }
    }
}
