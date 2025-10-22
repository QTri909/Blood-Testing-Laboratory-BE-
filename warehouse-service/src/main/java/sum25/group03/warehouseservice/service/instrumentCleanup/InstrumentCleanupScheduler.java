package sum25.group03.warehouseservice.service.instrumentCleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentCleanupScheduler {
    private final InstrumentRepo instrumentRepo;
    @Scheduled(cron = "0 0 0 * * ?") // run daily at 00:00
    @Transactional
    public void autoDeleteInactiveInstruments() {
        LocalDate threshold = LocalDate.now().minusMonths(3);
        List<Instrument> oldInactive = instrumentRepo.findInactiveBefore(threshold);
        instrumentRepo.deleteAll(oldInactive);
        log.info("Deleted {} inactive instruments older than 3 months", oldInactive.size());
    }
}
