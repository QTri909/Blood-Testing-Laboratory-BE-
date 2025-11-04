package sum25.group03.warehouseservice.service.reagentinventory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentInventoryServiceImpl implements ReagentInventoryService {
    private final ReagentInventoryRepo reagentInventoryRepo;

    @Transactional
    @Override
    public void decreaseQuantity(Long reagentId, String lotNumber, double quantityUsed) {
        ReagentInventory inventory = reagentInventoryRepo
                .findByReagent_ReagentIdAndLotNumber(reagentId, lotNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Reagent lot not found"));

        if (inventory.getQuantityAvailable() < quantityUsed) {
            throw new IllegalArgumentException("Not enough quantity available");
        }

        double newQuantity = inventory.getQuantityAvailable() - quantityUsed;
        inventory.setQuantityAvailable(newQuantity);

        if (newQuantity <= 0) {
            inventory.setStatus(ReagentInventoryStatus.EMPTY);
        }

        reagentInventoryRepo.save(inventory);

        log.info("Decreased quantity for reagent {} (lot {}) by {} units", reagentId, lotNumber, quantityUsed);
    }
}
