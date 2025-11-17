package sum25.group03.warehouseservice.service.instrument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import sum25.group03.common.response.events.InstrumentModeChangedEvent;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstrumentEventListener {

    private final InstrumentRepo instrumentRepo;

    @KafkaListener(topics = "instrument-mode-changed-events", groupId = "warehouse-service-group",
            containerFactory = "instrumentModeKafkaListenerContainerFactory")
    public void handleInstrumentModeChangedEvent(@Payload InstrumentModeChangedEvent event) {
        try {
            log.info("Received instrument mode changed event for instrument ID: {} - {} to {}",
                    event.getInstrumentId(), event.getPreviousStatus(), event.getNewStatus());

            Instrument instrument = instrumentRepo.findById(event.getInstrumentId())
                    .orElseThrow(() -> {
                        log.error("Instrument not found with ID: {}", event.getInstrumentId());
                        return new RuntimeException("Instrument not found with ID: " + event.getInstrumentId());
                    });

            log.info("Instrument found: {} ", instrument.getInstrumentName());
            String newStatus = event.getNewStatus();

            if ("INACTIVE".equals(newStatus) || "MAINTENANCE".equals(newStatus)){
                instrument.setStatus(InstrumentStatus.INACTIVE);
                instrument.setDeactivatedAt(LocalDate.now());

                Instrument updatedInstrument = instrumentRepo.save(instrument);

                log.info("Instrument deactivated successfully - Instrument ID: {}, Reason: {}, " +
                                "Deactivated at: {}",
                        event.getInstrumentId(), event.getReason(), updatedInstrument.getDeactivatedAt());
            }else {
                instrument.setStatus(InstrumentStatus.ACTIVE);
                instrument.setDeactivatedAt(LocalDate.now());
                Instrument updatedInstrument = instrumentRepo.save(instrument);
                log.info("Instrument active successfully - Instrument ID: {}, Reason: {}, " +
                                "Active at: {}",
                        event.getInstrumentId(), event.getReason(), updatedInstrument.getDeactivatedAt());
            }

        } catch (Exception e) {
            log.error("Error processing instrument mode changed event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process instrument mode changed event", e);
        }
    }
}
