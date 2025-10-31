package sum25.group03.warehouseservice.service.reagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.warehouseservice.event.ReagentInstalledEvent;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.event.UpdateExpiryReagent;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentEventListener {

    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;

    @KafkaListener(topics = "reagent-installed-events", groupId = "warehouse-service-group")
    public void handleReagentInstalledEvent(@Payload ReagentInstalledEvent event) {
        try {
            log.info("Received reagent installed event for reagent ID: {} (Batch: {})",
                    event.getReagentId(), event.getLotNumber());

            ReagentInventory reagentInventory = reagentInventoryRepo.findByLotNumber(event.getLotNumber())
                    .orElseThrow(() -> {
                        log.error("Reagent not found in lot: {}", event.getLotNumber());
                        return new RuntimeException("Reagent not found in lot: " + event.getLotNumber());
                    });

            log.info("Reagent in lot found: {} - Current quantity: {}", reagentInventory.getLotNumber(), reagentInventory.getQuantityAvailable());

            double newQuantity = reagentInventory.getQuantityAvailable() - event.getRequiredVolume();

            if (newQuantity < 0) {
                log.warn("Quantity would be negative after installation. Current: {}, Required: {}",
                        reagentInventory.getQuantityAvailable(), event.getRequiredVolume());
                newQuantity = 0;
            }

            reagentInventory.setQuantityAvailable(newQuantity);
            ReagentInventory updatedReagentInventory = reagentInventoryRepo.save(reagentInventory);


            log.info("Reagent quantity updated successfully - Reagent ID: {}, New quantity: {}, " +
                            "Installed on instrument: {} (ID: {})",
                    event.getReagentId(), updatedReagentInventory.getQuantityAvailable(),
                    event.getInstrumentName(), event.getInstrumentId());

        } catch (Exception e) {
            log.error("Error processing reagent installed event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process reagent installed event", e);
        }
    }


    @KafkaListener(topics = "update-expiry-reagent", groupId = "warehouse-service-group")
    public void handleUpdateExpiryReagent(@Payload UpdateExpiryReagent event) {
        try {
            ReagentInventory reagentInventory = reagentInventoryRepo.findByLotNumber(event.getLotNumber())
                    .orElseThrow(() -> {
                        log.error("Reagent not found in lot: {}", event.getLotNumber());
                        return new RuntimeException("Reagent not found in lot: " + event.getLotNumber());
                    });
            reagentInventory.setStatus(ReagentInventoryStatus.EXPIRED);

            log.info("Marking reagent as EXPIRED - Lot: {}, Expiration Date: {}",
                    reagentInventory.getLotNumber(), reagentInventory.getExpiryDate());


        } catch (Exception e) {
            log.error("Error processing update expiry reagent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process update expiry reagent", e);
        }
    }
}
