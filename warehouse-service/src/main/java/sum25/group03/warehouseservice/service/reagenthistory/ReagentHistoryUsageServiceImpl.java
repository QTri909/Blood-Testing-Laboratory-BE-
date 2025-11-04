package sum25.group03.warehouseservice.service.reagenthistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sum25.group03.warehouseservice.audit.service.AuditLogService;
import sum25.group03.warehouseservice.dto.request.ReagentUsageReq;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsageMiniRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.exception.MissingRequiredFieldsException;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;
import sum25.group03.warehouseservice.service.reagentinventory.ReagentInventoryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentHistoryUsageServiceImpl implements ReagentHistoryUsageService {
    private final ReagentUsageRepo reagentUsageRepo;
    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;
    private final AuditLogService auditLogService;
    private final ReagentInventoryService reagentInventoryService;
    private final InstrumentRepo instrumentRepo;

    @Override
    public Page<ReagentRes> filterReagentsWithUsage(String name, Pageable pageable) {
        Page<Reagents> reagents = reagentRepo.filterReagents(name, pageable);

        return reagents.map(reagent -> {

            List<ReagentUsageMiniRes> usages = reagentUsageRepo
                    .findAllByReagentOrderByUsedAtDesc(reagent)
                    .stream()
                    .map(u -> ReagentUsageMiniRes.builder()
                            .usageId(u.getReagentHistoryUsageId())
                            .usageType(u.getUsageType())
                            .quantityUsed(u.getQuantityUsed())
                            .unit(u.getUnit())
                            .usedAt(u.getUsedAt().toString())
                            .performedBy("system")
                            .build())
                    .toList();

            return mapToReagentRes(reagent, usages);
        });
    }

    @Override
    public void useReagent(ReagentUsageReq req) {

        if (req.getReagentId() == null) {
            throw new MissingRequiredFieldsException("Reagent ID is required.");
        }
        if (req.getInstrumentId() == null) {
            throw new MissingRequiredFieldsException("Instrument ID is required.");
        }
        if (req.getUserId() == null) {
            throw new MissingRequiredFieldsException("User ID is required.");
        }
        if (req.getLotNumber() == null || req.getLotNumber().isBlank()) {
            throw new MissingRequiredFieldsException("Lot number cannot be empty.");
        }
        if (req.getQuantity() <= 0) {
            throw new MissingRequiredFieldsException("Quantity used must be greater than 0.");
        }

        Reagents reagents = reagentRepo.findById(req.getReagentId()).orElseThrow(() -> {
            log.error("Reagent with id {} not found", req.getReagentId());
            return new NotFoundException("Reagent not found");
        });

        Instrument instrument = instrumentRepo.findById(req.getInstrumentId()).orElseThrow(() -> {
            log.error("Instrument with id {} not found", req.getInstrumentId());
            return new NotFoundException("Instrument not found");
        });

        // Cập nhật tồn kho
        try {
            reagentInventoryService.decreaseQuantity(req.getReagentId(),
                    req.getLotNumber(),
                    req.getQuantity());
        } catch (Exception e) {
            log.error("Failed to update inventory for reagent {} - lot {}: {}",
                    req.getReagentId(), req.getLotNumber(), e.getMessage());
            throw new IllegalStateException("Unable to decrease reagent inventory: " + e.getMessage());
        }
        // Ghi vào history
        ReagentHistoryUsage usage = ReagentHistoryUsage.builder()
                .reagent(reagents)
                .instrument(instrument)
                .quantityUsed(req.getQuantity())
                .usedBy(req.getUserId().intValue())
                .lotNumber(req.getLotNumber())
                .usedAt(LocalDate.now())
                .build();

        reagentUsageRepo.save(usage);

        auditLogService.logWrite(
                "CREATE_USAGE_HISTORY",
                "ReagentHistoryUsage",
                String.valueOf(usage.getReagentHistoryUsageId()),
                "Auto log new reagent usage for lot " + req.getLotNumber()
        );
    }

    private ReagentRes mapToReagentRes(Reagents reagent, List<ReagentUsageMiniRes> usages) {
        Integer totalQuantity = reagentInventoryRepo.getTotalQuantityByReagentId(reagent.getReagentId());

        return ReagentRes.builder()
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .catalogNumber(reagent.getCatalogNumber())
                .casNumber(reagent.getCasNumber())
                .unit(reagent.getUnit())
                .quantity(totalQuantity != null ? totalQuantity : 0)
                .usages(usages)
                .build();
    }
}
