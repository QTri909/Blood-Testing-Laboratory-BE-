package sum25.group03.warehouseservice.service.reagentfilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.mapper.VendorMapper;
import sum25.group03.warehouseservice.repository.HistorySupplyRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentFilterServiceImpl implements ReagentFilterService {
    private final ReagentUsageRepo reagentUsageRepo;

    @Override
    public PageRes<HistoryUsageRes> filterUsageHistory(String reagentName, LocalDate startDate, LocalDate endDate, String sortBy, String direction, Pageable pageable) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<ReagentHistoryUsage> usagePage = reagentUsageRepo
                .filterUsageHistory(reagentName, startDate, endDate, pageable);

        List<HistoryUsageRes> usageResList = usagePage.getContent().stream()
                .map(u -> HistoryUsageRes.builder()
                        .usageId(u.getReagentHistoryUsageId())
                        .reagent(ReagentRes.builder()
                                .reagentId(u.getReagent().getReagentId())
                                .reagentName(u.getReagent().getReagentName())
                                .catalogNumber(u.getReagent().getCatalogNumber())
                                .casNumber(u.getReagent().getCasNumber())
                                .unit(u.getReagent().getUnit())
                                .build())
                        .usageType(u.getUsageType())
                        .quantityUsed(u.getQuantityUsed())
                        .unit(u.getUnit())
                        .usedAt(u.getUsedAt())
                        .usedBy(u.getUsedBy())
                        .lotNumber(u.getLotNumber())
                        .notes(u.getNotes())
                        .instrumentName(u.getInstrument().getInstrumentName())
                        .build())
                .toList();
        return PageRes.<HistoryUsageRes>builder()
                .content(usageResList)
                .pageNumber(usagePage.getNumber())
                .pageSize(usagePage.getSize())
                .totalElements(usagePage.getTotalElements())
                .totalPages(usagePage.getTotalPages())
                .build();
    }
}
