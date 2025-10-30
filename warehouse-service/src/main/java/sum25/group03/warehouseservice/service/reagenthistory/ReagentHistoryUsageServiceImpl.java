package sum25.group03.warehouseservice.service.reagenthistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsageMiniRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

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

    @Override
    public Page<ReagentRes> filterReagentsWithUsage(String name, Pageable pageable) {
        Page<Reagents> reagents = reagentRepo.filterReagents(name, pageable);

        return reagents.map(reagent -> {

            List<ReagentUsageMiniRes> usages = reagentUsageRepo
                    .findTop3ByReagentOrderByUsedAtDesc(reagent)
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

    private ReagentRes mapToReagentRes(Reagents reagent, List<ReagentUsageMiniRes> usages) {
        Integer totalQuantity = reagentInventoryRepo.getTotalQuantityByReagentId(reagent.getReagentId());

        return ReagentRes.builder()
                .reagentId(reagent.getReagentId())
                .reagentName(reagent.getReagentName())
                .catalogNumber(reagent.getCatalogNumber())
                .casNumber(reagent.getCasNumber())
                .unit(reagent.getUnit())
                .expirationDate(
                        reagent.getExpirationDate() != null
                                ? reagent.getExpirationDate().toString()
                                : null
                )
                .quantity(totalQuantity != null ? totalQuantity : 0)
                .usages(usages)
                .build();
    }
}
