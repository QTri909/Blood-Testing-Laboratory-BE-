package sum25.group03.instrumentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.common.InstalledReagentStatus;
import sum25.group03.instrumentservice.model.InstalledReagent;
import sum25.group03.instrumentservice.repository.InstalledReagentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentStatusSchedulerService {
    private final InstalledReagentRepository installedReagentRepository;


    @Scheduled(cron = "0 0 0 * * *")
    public void checkReagentExpiration() {
        log.info("Starting scheduled reagent expiration check job");

        try {
            LocalDate today = LocalDate.now();

            List<InstalledReagent> reagentsToCheck = installedReagentRepository.findByStatusNotIn(
                    List.of(InstalledReagentStatus.EXPIRED, InstalledReagentStatus.REMOVED)
            );

            log.info("Found {} reagents to check for expiration", reagentsToCheck.size());

            int expiredCount = 0;
            for (InstalledReagent reagent : reagentsToCheck) {
                if (reagent.getExpirationDate() != null && reagent.getExpirationDate().isBefore(today)) {
                    log.info("Reagent ID: {} has expired (expiration date: {}). Updating status to EXPIRED",
                            reagent.getId(), reagent.getExpirationDate());

                    reagent.setStatus(InstalledReagentStatus.EXPIRED);
                    installedReagentRepository.save(reagent);
                    expiredCount++;
                }
            }

            log.info("Reagent expiration check completed. {} reagents marked as EXPIRED", expiredCount);

        } catch (Exception e) {
            log.error("Error during reagent expiration check job: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkReagentVolume() {
        log.info("Starting scheduled reagent volume check job");

        try {
            List<InstalledReagent> inUseReagents = installedReagentRepository.findByStatus(InstalledReagentStatus.IN_USE);
            List<InstalledReagent> lowVolumeReagents = installedReagentRepository.findByStatus(InstalledReagentStatus.LOW_VOLUME);

            int lowVolumeCount = 0;
            int emptyCount = 0;

            for (InstalledReagent reagent : inUseReagents) {
                if (reagent.getCurrentVolume() != null && reagent.getCurrentVolume() < 50) {
                    log.info("Reagent ID: {} volume is below (current: {}). Updating status to LOW_VOLUME",
                            reagent.getId(), reagent.getCurrentVolume());

                    reagent.setStatus(InstalledReagentStatus.LOW_VOLUME);
                    installedReagentRepository.save(reagent);
                    lowVolumeCount++;
                }
            }


            for (InstalledReagent reagent : lowVolumeReagents) {
                if (reagent.getCurrentVolume() != null && reagent.getCurrentVolume() == 0) {
                    log.info("Reagent ID: {} volume is empty (current: 0). Updating status to EMPTY",
                            reagent.getId());

                    reagent.setStatus(InstalledReagentStatus.EMPTY);
                    installedReagentRepository.save(reagent);
                    emptyCount++;
                }
            }

            log.info("Reagent volume check completed. {} reagents marked as LOW_VOLUME, {} marked as EMPTY",
                    lowVolumeCount, emptyCount);

        } catch (Exception e) {
            log.error("Error during reagent volume check job: {}", e.getMessage(), e);
        }
    }
}
