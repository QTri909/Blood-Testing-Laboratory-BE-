package sum25.group03.warehouseservice.service.instrumentstatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.exception.InvalidArgumentException;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Instrument_Status_Service")
public class InstrumentStatusServiceImpl implements InstrumentStatusService {
    private final InstrumentRepo instrumentRepo;

    @Override
    public void activateInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        if(isSameStatus(instrument, InstrumentStatus.ACTIVE)){
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
        if (isSameStatus(instrument, InstrumentStatus.INACTIVE)) {
            log.info("Instrument {} is already INACTIVE", id);
            return;
        }
        instrument.setStatus(InstrumentStatus.INACTIVE);
        instrument.setAutoDeleteScheduledAt(LocalDate.now().plusMonths(3));
        instrument.setUpdatedAt(LocalDate.now());
        instrument.setDeactivatedAt(LocalDate.now());
        instrumentRepo.save(instrument);
        log.info("User '{}' deactivated instrument {}", username, id);
    }

    @Override
    public void deleteInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        if (!isSameStatus(instrument, InstrumentStatus.INACTIVE)) {
            throw new InvalidArgumentException("Only INACTIVE instruments can be deleted");
        }
        instrument.setStatus(InstrumentStatus.DELETED);
        instrument.setAutoDeleteScheduledAt(null);
        instrument.setUpdatedAt(LocalDate.now());
        instrumentRepo.save(instrument);
        log.info("User '{}' deleted instrument {}", username, id);
    }

    private Instrument getInstrumentOrThrow(Long id) {
        return instrumentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + id));
    }

    private boolean isSameStatus(Instrument instrument, InstrumentStatus expected) {
        return instrument.getStatus() != null && instrument.getStatus().equals(expected);
    }
}
