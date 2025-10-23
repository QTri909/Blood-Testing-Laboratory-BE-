package sum25.group03.warehouseservice.service.instrumentstatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.event.InstrumentEvent;
import sum25.group03.warehouseservice.exception.InvalidArgumentException;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Instrument_Status_Service")
public class InstrumentStatusServiceImpl implements InstrumentStatusService {
    private final InstrumentRepo instrumentRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "instrument-events";

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
        // publish event
        publishEvent(instrument, "ACTIVATE", username, "Instrument activated successfully");
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
        publishEvent(instrument, "DEACTIVATE", username, "Instrument deactivated. Scheduled for deletion in 3 months");
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
        publishEvent(instrument, "DELETE", username, "Instrument deleted from system");
    }

    private void publishEvent(Instrument instrument, String eventType, String username, String detail) {
        InstrumentEvent event = InstrumentEvent.builder()
                .instrumentId(instrument.getInstrumentId())
                .instrumentName(instrument.getInstrumentName())
                .eventType(eventType)
                .performedBy(username)
                .status(instrument.getStatus())
                .timestamp(LocalDate.now())
                .details(detail)
                .build();

        kafkaTemplate.send(TOPIC, event);
        log.info("Published Kafka event: {}", event);
    }

    private Instrument getInstrumentOrThrow(Long id) {
        return instrumentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id: " + id));
    }

    private boolean isSameStatus(Instrument instrument, InstrumentStatus expected) {
        return instrument.getStatus() != null && instrument.getStatus().equals(expected);
    }
}
