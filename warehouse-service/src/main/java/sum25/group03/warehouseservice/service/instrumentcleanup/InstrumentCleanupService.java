package sum25.group03.warehouseservice.service.instrumentcleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.audit.service.AuditLogService;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentCleanupService {

    private final InstrumentRepo instrumentRepo;
    private final AuditLogService auditLogService;

    @Scheduled(cron = "0 0 0 * * *") // daily at 00:00 midnight
    public void autoDeleteInactiveInstruments() {
        List<Instrument> expired = instrumentRepo.findByStatusAndAutoDeleteScheduledAtBefore(InstrumentStatus.INACTIVE, LocalDate.now());
        expired.forEach(instrument -> {
            Long id = instrument.getInstrumentId();
            String name = instrument.getInstrumentName();

            instrument.setStatus(InstrumentStatus.DELETED);
            instrument.setAutoDeleteScheduledAt(null);
            instrumentRepo.save(instrument);

            auditLogService.logWrite(
                    "AutoDeleteInstrument",
                    "Instrument",
                    id.toString(),
//                    ipAddress,
//                    userAgent,
                    "Instrument" + name + " (ID " + id + ") deleted automatically after inactivity"
            );

            log.info("Instrument {} has been deleted", instrument.getInstrumentId());
        });
    }
}
