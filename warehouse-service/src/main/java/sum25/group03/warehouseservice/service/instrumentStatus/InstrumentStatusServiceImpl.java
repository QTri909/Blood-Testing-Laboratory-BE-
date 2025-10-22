package sum25.group03.warehouseservice.service.instrumentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Instrument_Rotate_Service")
public class InstrumentStatusServiceImpl implements InstrumentStatusService {
    private final InstrumentRepo instrumentRepo;

    @Override
    public void activateInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        if(instrument.getStatus().equals(InstrumentStatus.ACTIVE)){
            log.info("Instrument {} is already ACTIVE", id);
            return;
        }
        instrument.setStatus(InstrumentStatus.ACTIVE);
        instrument.setUpdatedAt(LocalDate.now());
        instrument.setAutoDeleteScheduledAt(null);
        instrumentRepo.save(instrument);
        log.info("User '{}' activated instrument {}", username, id);
    }

    @Override
    public void deactivateInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        if (instrument.getStatus() == InstrumentStatus.INACTIVE) {
            log.info("Instrument {} is already INACTIVE", id);
            return;
        }
        instrument.setStatus(InstrumentStatus.INACTIVE);
        instrument.setAutoDeleteScheduledAt(LocalDate.now().plusMonths(3));
        instrument.setUpdatedAt(LocalDate.now());
        instrumentRepo.save(instrument);
        log.info("User '{}' deactivated instrument {}", username, id);
    }

    @Override
    public void deleteInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        if (instrument.getStatus() != InstrumentStatus.INACTIVE) {
            throw new IllegalStateException("Only INACTIVE instruments can be deleted");
        }
        instrument.setStatus(InstrumentStatus.DELETED);
        instrument.setAutoDeleteScheduledAt(null);
        instrument.setUpdatedAt(LocalDate.now());
        instrumentRepo.save(instrument);
        log.info("User '{}' deleted instrument {}", username, id);
    }

    @Override
    public InstrumentStatusResponse checkInstrumentStatus(Long id) {
        Instrument instrument = getInstrumentOrThrow(id);
        OperationalStatus currentStatus = instrument.getOperationalStatus();
        String message = "Instrument is currently in " + currentStatus + " state.";

        if (instrument.getOperationalStatus() == OperationalStatus.ERROR) {
            boolean fixed = performAutoRecheck(instrument);

            if (fixed) {
                instrument.setOperationalStatus(OperationalStatus.READY);
                message = "Instrument has recovered from ERROR and is now READY.";
                log.info("Instrument {} recovered to READY state after recheck.", id);
            } else {
                log.warn("Instrument {} remains in ERROR state after recheck.", id);
                message = "Instrument remains in ERROR state after recheck. Please contact maintenance team.";
            }
            instrumentRepo.save(instrument);
            currentStatus = instrument.getOperationalStatus();
        }
        return InstrumentStatusResponse.builder()
                .instrumentId(instrument.getInstrumentId())
                .instrumentName(instrument.getInstrumentName())
                .status(currentStatus)
                .message(message)
                .checkedAt(LocalDate.now())
                .build();
    }

    @Override
    public void autoCleanupExpiredInstruments() {
        LocalDate now = LocalDate.now();
        List<Instrument> expired = instrumentRepo.findByStatusAndAutoDeleteScheduledAtBefore(InstrumentStatus.INACTIVE, now);
        if(expired.isEmpty()){
            log.info("No instruments found for auto-deletion.");
            return;
        }
        for(Instrument instrument : expired){
            instrument.setStatus(InstrumentStatus.DELETED);
            instrument.setAutoDeleteScheduledAt(null);
            instrument.setUpdatedAt(now);
            instrumentRepo.save(instrument);
            log.info("Instrument {} automatically deleted after 3 months", instrument.getInstrumentId());
        }
    }

    private boolean performAutoRecheck(Instrument instrument) {
        return Math.random() > 0.3; // 70% to fix
    }

    private Instrument getInstrumentOrThrow(Long id) {
        return instrumentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + id));
    }
}
