package sum25.group03.warehouseservice.service.reagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.audit.model.AuditLog;
import sum25.group03.warehouseservice.audit.service.AuditLogService;
import sum25.group03.warehouseservice.dto.request.ReagentReq;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.common.response.events.DeleteReagentEvent;
import sum25.group03.warehouseservice.entity.enums.ReagentUnit;
import sum25.group03.warehouseservice.event.ReagentCreatedEvent;
import sum25.group03.warehouseservice.exception.DuplicateException;
import sum25.group03.warehouseservice.exception.InvalidArgumentException;
import sum25.group03.warehouseservice.exception.MissingRequiredFieldsException;
import sum25.group03.common.response.events.DeleteReagentEvent;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentServiceImpl implements ReagentService {
    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;
    private final ReagentUsageRepo reagentUsageRepo;
    private final KafkaTemplate<String, DeleteReagentEvent> kafkaDeleteTemplate;
    private final AuditLogService auditLogService;
    private final KafkaTemplate<String, ReagentCreatedEvent> kafkaCreateTemplate;

    @Override
    public List<Long> findExistingIds(List<Long> reagentIds) {
        return reagentRepo.findExistingIds(reagentIds);
    }

    @Override
    public List<Reagents> findAllByInstrumentId(Long instrumentId) {
        return reagentRepo.findAllByInstrumentId(instrumentId);
    }

    @Override
    public List<Reagents> findAllByReagentIdAndStatus(List<Long> reagentId, ReagentStatus status) {
        return reagentRepo.findAllByReagentIdInAndStatus(reagentId,status);
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
                .unit(reagent.getReagent().getUnit().getUnit())
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
            response.setUnit(reagent.getUnit().getUnit());
            response.setReagentName(reagent.getReagentName());
            response.setUsageMin(reagent.getUsageMin());
            response.setUsageMax(reagent.getUsageMax());
            return response;
        }).toList();
    }

    @Override
    public List<ReagentRes> getAllReagents() {
        List<Reagents> reagents = reagentRepo.findAllByStatus(ReagentStatus.ACTIVE);
        return reagents.stream().map(reagent -> {
            ReagentRes res = new ReagentRes();
            res.setReagentId(reagent.getReagentId());
            res.setReagentName(reagent.getReagentName());
            res.setCatalogNumber(reagent.getCatalogNumber());
            res.setCasNumber(reagent.getCasNumber());
            return res;
        }).toList();
    }
    @Override
    public PageRes<ReagentListItemRes> getReagentListItems(int page, int size) {

        Page<Reagents> reagentsPage = reagentRepo.findAllByStatus(ReagentStatus.ACTIVE, PageRequest.of(page, size));

        List<Long> reagentIds = reagentsPage.stream()
                .map(Reagents::getReagentId)
                .toList();

        List<ReagentInventory> inventories = reagentIds.isEmpty()
                ? List.of()
                : reagentInventoryRepo.findAllByReagentIdIn(reagentIds);

        Map<Long, Double> inventoryMap = inventories.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getReagent().getReagentId(),
                        Collectors.summingDouble(ReagentInventory::getQuantityAvailable)
                ));

        List<ReagentListItemRes> items = reagentsPage.stream().map(reagent -> {
            double totalQty = inventoryMap.getOrDefault(reagent.getReagentId(), 0.0);
            Integer maxLevelInt = reagent.getMaxStockLevel();
            double maxLevel = maxLevelInt == null ? 0.0 : maxLevelInt.doubleValue();
            double lowLevel = reagent.getMinStockLevel().doubleValue();

            return ReagentListItemRes.builder()
                    .reagentId(reagent.getReagentId())
                    .reagentName(reagent.getReagentName())
                    .catalogNumber(reagent.getCatalogNumber())
                    .totalStock(totalQty)
                    .unit(reagent.getUnit().getUnit())
                    .maxStockLevel(maxLevel)
                    .lowStockLevel(lowLevel)
                    .build();
        }).toList();

        // Build PageRes from reagentsPage metadata
        return PageRes.<ReagentListItemRes>builder()
                .content(items)
                .pageNumber(reagentsPage.getNumber())
                .pageSize(reagentsPage.getSize())
                .totalElements(reagentsPage.getTotalElements())
                .totalPages(reagentsPage.getTotalPages())
                .build();
    }

    @Override
    public ReagentDetailRes getReagentDetail(Long reagentId) {
        // Load reagent, throw if not found
        Reagents reagent = reagentRepo.findById(reagentId)
                .orElseThrow(() -> new NotFoundException("Reagent not found with id: " + reagentId));

        // total stock (may return null)
//        Double totalStockObj = reagentInventoryRepo.getTotalQuantitysByReagentId(reagentId);
//        double totalStock = totalStockObj == null ? 0.0 : totalStockObj;

        // fetch all inventories (lots) for this reagent
        List<ReagentInventory> inventories = reagentInventoryRepo.findAllByReagentIdIn(List.of(reagentId));

        List<ReagentInventoryRes> inventoryResList = inventories.stream().map(inv -> {
            ReagentInventoryStatus status = inv.getStatus();
            return ReagentInventoryRes.builder()
                    .reagentInventoryId(inv.getReagentInventoryId())
                    .lotNumber(inv.getLotNumber())
                    .quantityAvailable(inv.getQuantityAvailable())
                    .expiryDate(inv.getExpiryDate())
                    .status(status)
                    .build();
        }).toList();

        // min / max stock level with null-safety
        Integer maxLevelInt = reagent.getMaxStockLevel();
        double maxStock = maxLevelInt == null ? 0.0 : maxLevelInt.doubleValue();

        return ReagentDetailRes.builder()
                .reagentId(reagent.getReagentId())
//                .reagentName(reagent.getReagentName())
//                .catalogNumber(reagent.getCatalogNumber())
                .description(reagent.getStorageConditions())
//                .maxStockLevel(maxStock)
//                .totalStock(totalStock)
                .inventories(inventoryResList)
                .build();
    }

    @Override
    public ReagentRes createReagent(ReagentReq req) {
        log.info("[CREATE-REAGENT] Request received to create reagent. Name={}, Catalog={}",
                req.getReagentName(), req.getCatalogNumber());
        // VALIDATE
        if (reagentRepo.existsByReagentName(req.getReagentName())) {
            throw new DuplicateException("Reagent name already exists");
        }

        if (reagentRepo.existsByCatalogNumber(req.getCatalogNumber())) {
            throw new DuplicateException("Catalog number already exists");
        }

        if (req.getUsageMin() != null && req.getUsageMax() != null) {
            if (req.getUsageMax() < req.getUsageMin()) {
                throw new InvalidArgumentException("usageMax must be greater than or equal to usageMin");
            }
        }

        if (req.getMinStockLevel() > req.getMaxStockLevel()) {
            throw new InvalidArgumentException("minStockLevel cannot be greater than maxStockLevel");
        }

        Reagents reagent = Reagents.builder()
                .reagentName(req.getReagentName())
                .catalogNumber(req.getCatalogNumber())
                .casNumber(req.getCasNumber())
                .unit(ReagentUnit.ML)
                .storageConditions(req.getStorageConditions())
                .status(ReagentStatus.ACTIVE) // default to ACTIVE
                .maxStockLevel(req.getMaxStockLevel())
                .minStockLevel(req.getMinStockLevel())
                .usageMin(req.getUsageMin())
                .usageMax(req.getUsageMax())
                .build();

        Reagents saved = reagentRepo.save(reagent);
        log.info("[CREATE-REAGENT] Reagent created successfully. ID={}, Name={}",
                saved.getReagentId(), saved.getReagentName());

        List<AuditLog.FieldChange> changes = auditLogService.createFieldChanges(
                Map.of(
                        "reagentName", req.getReagentName(),
                        "catalogNumber", req.getCatalogNumber(),
                        "casNumber", req.getCasNumber(),
                        "unit", req.getUnit().getUnit(),
                        "storageConditions", req.getStorageConditions(),
                        "status", "ACTIVE",
                        "maxStockLevel", req.getMaxStockLevel().toString(),
//                        "minStockLevel", req.getMinStockLevel().toString(),
                        "usageMin", req.getUsageMin() == null ? "null" : req.getUsageMin().toString(),
                        "usageMax", req.getUsageMax() == null ? "null" : req.getUsageMax().toString()
                )
        );

        auditLogService.logWrite(
                "CreateReagent",
                "Reagent",
                saved.getReagentId().toString(),
                "0.0.0.0",
                "Mozilla/5.0",
                changes
        );
        log.info("[AUDIT] Logged creation for Reagent ID={}", saved.getReagentId());

//        ReagentCreatedEvent event = new ReagentCreatedEvent(
//                saved.getReagentId(),
//                saved.getReagentName(),
//                saved.getCatalogNumber(),
//                saved.getCasNumber()
//        );
//        kafkaCreateTemplate.send("reagent-created-events", event);
//        log.info("Sent reagent created event for reagent id: {}", saved.getReagentId());

        return ReagentRes.builder()
                .reagentId(saved.getReagentId())
                .reagentName(saved.getReagentName())
                .catalogNumber(saved.getCatalogNumber())
                .casNumber(saved.getCasNumber())
                .unit(saved.getUnit().getUnit())
                .quantity(0)
                .build();
    }

    @Override
    public ReagentRes getReagentById(Long reagentId) {
        Reagents reagent = reagentRepo.findById(reagentId)
                .orElseThrow(() -> new NotFoundException("Reagent not found with id: " + reagentId));
        ReagentRes res = ReagentRes.builder()
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .catalogNumber(reagent.getCatalogNumber())
                .casNumber(reagent.getCasNumber())
                .unit(reagent.getUnit().getUnit())
                .storageConditions(reagent.getStorageConditions())
                .createdAt(reagent.getCreatedAt())
                .build();
        return res;
    }

    @Override
    public List<ReagentInventoryRes> getListLotNumberByReagentId(Long reagentId) {
        List<ReagentInventory> inventories = reagentInventoryRepo.findAllByReagentId(reagentId,ReagentInventoryStatus.AVAILABLE);

        return inventories.stream()
                .map(r -> ReagentInventoryRes.builder()
                        .reagentInventoryId(r.getReagentInventoryId())
                        .lotNumber(r.getLotNumber())
                        .quantityAvailable(r.getQuantityAvailable())
                        .build())
                .toList();
    }

}
