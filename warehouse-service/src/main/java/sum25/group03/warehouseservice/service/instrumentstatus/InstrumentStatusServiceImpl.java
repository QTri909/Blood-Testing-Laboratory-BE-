package sum25.group03.warehouseservice.service.instrumentstatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.audit.model.AuditLog;
import sum25.group03.warehouseservice.audit.service.AuditLogService;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.event.InstrumentEvent;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "Instrument_Status_Service")
public class InstrumentStatusServiceImpl implements InstrumentStatusService {
    private final InstrumentRepo instrumentRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditLogService auditLogService;
    private static final String TOPIC = "instrument-events";

    @Override
    public void activateInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        InstrumentStatus previousStatus = instrument.getStatus();
        InstrumentStatus newStatus = InstrumentStatus.ACTIVE;

        validateStatusTransition(previousStatus, newStatus);

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

        List<AuditLog.FieldChange> changes = auditLogService.createFieldChanges(
                "status",
                previousStatus.toString(),
                newStatus.toString()
        );

        auditLogService.logWrite(
                "ActivateInstrument",
                "Instrument",
                instrument.getInstrumentId().toString(),
                "0.0.0.0", // hoặc ipAddress nếu có
                "Mozilla/5.0",
                changes
        );
    }

    @Override
    public void deactivateInstrument(Long id, String username) {
        Instrument instrument = getInstrumentOrThrow(id);
        InstrumentStatus previousStatus = instrument.getStatus();
        InstrumentStatus newStatus = InstrumentStatus.INACTIVE;

        validateStatusTransition(previousStatus, newStatus);

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

        List<AuditLog.FieldChange> changes = auditLogService.createFieldChanges(
                "status",
                previousStatus.toString(),
                newStatus.toString()
        );

        auditLogService.logWrite(
                "ActivateInstrument",
                "Instrument",
                instrument.getInstrumentId().toString(),
                "0.0.0.0", // hoặc ipAddress nếu có
                "Mozilla/5.0",
                changes
        );
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

    private void validateStatusTransition(InstrumentStatus currentStatus, InstrumentStatus newStatus) {
        if (currentStatus == InstrumentStatus.ACTIVE && newStatus != InstrumentStatus.INACTIVE) {
            throw new IllegalStateException("Invalid transition: ACTIVE can only go to INACTIVE");
        }
        // Cho phép INACTIVE có thể đi tới cả DELETED hoặc ACTIVE
        if (currentStatus == InstrumentStatus.INACTIVE &&
                (newStatus != InstrumentStatus.DELETED && newStatus != InstrumentStatus.ACTIVE)) {
            throw new IllegalStateException("Invalid transition: INACTIVE can only go to DELETED or ACTIVE");
        }
        if (currentStatus == InstrumentStatus.DELETED && newStatus != InstrumentStatus.ACTIVE) {
            throw new IllegalStateException("Invalid transition: DELETED can only go to ACTIVE");
        }
    }

}
