package sum25.group03.warehouseservice.service.reagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.event.ReagentInstalledEvent;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.repository.ReagentRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentEventListener {

    private final ReagentRepo reagentRepo;

    @KafkaListener(topics = "reagent-installed-events", groupId = "warehouse-service-group")
    public void handleReagentInstalledEvent(@Payload ReagentInstalledEvent event) {
        try {
            log.info("Received reagent installed event for reagent ID: {} (Batch: {})",
                    event.getReagentId(), event.getBatchNumber());

            Reagents reagent = reagentRepo.findById(event.getReagentId())
                    .orElseThrow(() -> {
                        log.error("Reagent not found with ID: {}", event.getReagentId());
                        return new RuntimeException("Reagent not found with ID: " + event.getReagentId());
                    });

            log.info("Reagent found: {} - Current quantity: {}", reagent.getReagentName(), reagent.getQuantity());

            int newQuantity = reagent.getQuantity() - event.getRequiredVolume().intValue();

            if (newQuantity < 0) {
                log.warn("Quantity would be negative after installation. Current: {}, Required: {}",
                        reagent.getQuantity(), event.getRequiredVolume());
                newQuantity = 0;
            }

            reagent.setQuantity(newQuantity);
            reagent.setInUse(true);

            Reagents updatedReagent = reagentRepo.save(reagent);

            log.info("Reagent quantity updated successfully - Reagent ID: {}, New quantity: {}, " +
                            "Installed on instrument: {} (ID: {})",
                    event.getReagentId(), updatedReagent.getQuantity(),
                    event.getInstrumentName(), event.getInstrumentId());

        } catch (Exception e) {
            log.error("Error processing reagent installed event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process reagent installed event", e);
        }
    }
}
