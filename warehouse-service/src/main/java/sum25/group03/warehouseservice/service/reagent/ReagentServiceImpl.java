package sum25.group03.warehouseservice.service.reagent;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.ReagentRepo;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentServiceImpl implements ReagentService {
    private final ReagentRepo reagentRepo;

    @Override
    public List<Long> findExistingIds(List<Long> reagentIds) {
        return reagentRepo.findExistingIds(reagentIds);
    }

    @Override
    public ReagentValidationResponse validateReagent(String batchNumber, Double requiredVolume) {
        log.info("Validating reagent with batch number: {}", batchNumber);


        Reagents reagent = reagentRepo.findByBatchNumber(batchNumber)
                .orElseThrow(() -> {
                    log.warn("[v0] Reagent not found with batch number: {}", batchNumber);
                    return new NotFoundException("Reagent not found with batch number: " + batchNumber);
                });

        log.info("Reagent found: {} (ID: {})", reagent.getReagentName(), reagent.getReagentId());


        if (reagent.isDeleted()) {
            log.warn("Reagent is deleted: {}", batchNumber);
            return buildInvalidResponse(reagent, "Reagent has been deleted and cannot be used");
        }

        LocalDate today = LocalDate.now();
        boolean isExpired = reagent.getExpirationDate().isBefore(today);
        if (isExpired) {
            log.warn("Reagent is expired: {} (Expiration: {})", batchNumber, reagent.getExpirationDate());
            return buildInvalidResponse(reagent, "Reagent has expired on " + reagent.getExpirationDate());
        }

        if (reagent.getStatus() == ReagentStatus.EMPTY) {
            log.warn("[Reagent has invalid status: {} (Status: {})", batchNumber, reagent.getStatus());
            return buildInvalidResponse(reagent, "Reagent status is " + reagent.getStatus() + " and cannot be used");
        }

        if(reagent.getQuantity() < requiredVolume) {
            log.warn("Reagent does not have sufficient quantity: {} (Available: {}, Required: {})",
                    batchNumber, reagent.getQuantity(), requiredVolume);
            return buildInvalidResponse(reagent, "Reagent does not have sufficient quantity. Available: "
                    + reagent.getQuantity() + ", Required: " + requiredVolume);
        }


        log.info("[v0] Reagent validation successful - reagent is valid and ready for use");


        return ReagentValidationResponse.builder()
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .batchNumber(reagent.getBatchNumber())
                .catalogNumber(reagent.getCatalogNumber())
                .expirationDate(reagent.getExpirationDate())
                .status(reagent.getStatus())
                .isValid(true)
                .isInInventory(true)
                .isNotExpired(true)
                .message("Reagent is valid and ready for installation")
                .build();
    }


    private ReagentValidationResponse buildInvalidResponse(Reagents reagent, String message) {
        return ReagentValidationResponse.builder()
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .batchNumber(reagent.getBatchNumber())
                .catalogNumber(reagent.getCatalogNumber())
                .expirationDate(reagent.getExpirationDate())
                .status(reagent.getStatus())
                .isValid(false)
                .isInInventory(!reagent.isDeleted())
                .isNotExpired(reagent.getExpirationDate().isAfter(LocalDate.now()))
                .message(message)
                .build();
    }
}
