package sum25.group03.warehouseservice.service.reagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.ReagentResponseForInstrument;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.common.response.events.DeleteReagentEvent;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentServiceImpl implements ReagentService {
    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;
    private final KafkaTemplate<String, DeleteReagentEvent> kafkaDeleteTemplate;

    @Override
    public List<Long> findExistingIds(List<Long> reagentIds) {
        return reagentRepo.findExistingIds(reagentIds);
    }

    @Override
    public List<Reagents> findAllByInstrumentId(Long instrumentId) {
        return reagentRepo.findAllByInstrumentId(instrumentId);
    }

    @Override
    public List<Reagents> findAllByReagentId(List<Long> reagentId) {
        return reagentRepo.findAllByReagentId(reagentId);
    }

    @Override
    public void deleteReagent(Long reagentId) {
        Reagents reagent = reagentRepo.findById(reagentId)
                .orElseThrow(() -> new NotFoundException("Reagent not found with id: " + reagentId));
        reagent.setStatus(ReagentStatus.DELETED);
        reagentRepo.save(reagent);
        log.info("Reagent with id {} has been marked as DELETED.", reagentId);
        // Send delete event to Kafka
        DeleteReagentEvent deleteReagentEvent = DeleteReagentEvent.builder()
                .reagentId(reagentId)
                .build();
        kafkaDeleteTemplate.send("reagent-deletions", deleteReagentEvent);
        log.info("Sent delete event for reagent id: {}", reagentId);
    }

    @Override
    public ReagentValidationResponse validateReagent(String lotNumber, Double requiredVolume) {
        log.info("Validating reagent with batch number: {}", lotNumber);


        ReagentInventory reagent = reagentInventoryRepo.findByLotNumber(lotNumber)
                .orElseThrow(() -> {
                    log.warn("Reagent not found with batch number: {}", lotNumber);
                    return new NotFoundException("Reagent not found with batch number: " + lotNumber);
                });

        log.info("Reagent found in lot: {} (ID: {})", reagent.getReagent().getReagentName(), reagent.getLotNumber());


        ReagentValidationResponse.ReagentValidationResponseBuilder responseBuilder = ReagentValidationResponse.builder()
                .reagentId(reagent.getReagent().getReagentId())
                .reagentName(reagent.getReagent().getReagentName())
                .unit(reagent.getReagent().getUnit())
                .lotNumber(reagent.getLotNumber())
                .catalogNumber(reagent.getReagent().getCatalogNumber())
                .expirationDate(reagent.getExpiryDate())
                .isInInventory(true);

        if (reagent.getReagent().getStatus().equals(ReagentStatus.DELETED)) {
            log.warn("Reagent is deleted: {}", lotNumber);
            return responseBuilder
                    .isValid(false)
                    .isNotExpired(true)
                    .message("Reagent has been deleted and cannot be used")
                    .build();
        }


        LocalDate today = LocalDate.now();
        boolean isExpired = reagent.getExpiryDate().isBefore(today);
        if (isExpired) {
            log.warn("Reagent is expired: {} (Expiration: {})", lotNumber, reagent.getExpiryDate());
            return responseBuilder
                    .isValid(false)
                    .isNotExpired(false)
                    .message("Reagent has expired on " + reagent.getExpiryDate())
                    .build();
        }

        if (reagent.getQuantityAvailable() == 0) {
            log.warn("[Reagent is empty in lot: {}", lotNumber);
            return responseBuilder
                    .isValid(false)
                    .isNotExpired(true)
                    .message("Reagent is empty and cannot be used")
                    .build();
        }

        if (reagent.getQuantityAvailable() < requiredVolume) {
            log.warn("Reagent does not have sufficient quantity: {} (Available: {}, Required: {})",
                    lotNumber, reagent.getQuantityAvailable(), requiredVolume);
            return responseBuilder
                    .isValid(false)
                    .isNotExpired(true)
                    .message("Reagent does not have sufficient quantity. Available: "
                            + reagent.getQuantityAvailable() + ", Required: " + requiredVolume)
                    .build();
        }


        log.info("Reagent validation successful - reagent is valid and ready for use");

        return responseBuilder
                .isValid(true)
                .isNotExpired(true)
                .message("Reagent is valid and ready for installation")
                .build();
    }

    @Override
    public List<ReagentResponseForInstrument> listReagentsForInstrument() {
        List<Reagents> reagents = reagentRepo.findAllDistinct();
        List<String> reagentNames = reagents.stream()
                .map(Reagents::getReagentName) // Lấy tên của từng thuốc thử
                .collect(Collectors.toList());

        // 2. In ra số lượng (sẽ là 10) và danh sách 10 cái tên đó
        log.info("Tìm thấy {} reagents: {}", reagents.size(), reagentNames.toString());
        return reagents.stream().map(reagent -> {
            ReagentResponseForInstrument response = new ReagentResponseForInstrument();
            response.setReagentId(reagent.getReagentId());
            response.setUnit(reagent.getUnit());
            response.setReagentName(reagent.getReagentName());
            response.setUsageMin(reagent.getUsageMin());
            response.setUsageMax(reagent.getUsageMax());
            return response;
        }).toList();
    }
}
