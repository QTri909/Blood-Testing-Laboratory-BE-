package sum25.group03.warehouseservice.service.reagenthistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.repository.ReagentHistoryUsageRepo;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentHistoryUsageServiceImpl implements ReagentHistoryUsageService {
    private final ReagentHistoryUsageRepo usageRepository;

    @Override
    public ReagentUsagePageResponse findAllUsageRecords(String reagentName, String sort, String usageType,
                                                        Long instrumentId, int page, int size) {
        log.info("Finding all reagent usage with name: {}, sort: {}, usageType: {}, instrumentId: {}, page: {}, size: {}",
                reagentName, sort, usageType, instrumentId, page, size);

        // Default sort
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "usedAt");

        Set<String> ALLOWED_SORTS = Set.of("usedAt","quantityUsed","usageType","unit");

        if (StringUtils.hasLength(sort)) {
            Matcher m = Pattern.compile("^(\\w+):(asc|desc)$", Pattern.CASE_INSENSITIVE).matcher(sort);
            if (m.find() && ALLOWED_SORTS.contains(m.group(1))) {
                order = new Sort.Order("asc".equalsIgnoreCase(m.group(2)) ? Sort.Direction.ASC : Sort.Direction.DESC, m.group(1));
            }
        }

        int pageNo = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<ReagentHistoryUsage> usageEntities =
                usageRepository.searchUsageRecords(
                        StringUtils.hasText(reagentName) ? reagentName.trim() : null,
                        StringUtils.hasText(usageType) ? usageType.trim() : null,
                        instrumentId,
                        pageable);

        return mapToUsagePageResponse(page, size, usageEntities);
    }

    @Override
    public List<ReagentUsageDetailResponse> findByReagentName(String reagentName) {
        log.info("Fetching usage records for reagent name: {}", reagentName);
        return usageRepository.findByReagent_ReagentNameContainingIgnoreCase(reagentName)
                .stream()
                .map(this::mapToUsageDetailResponse)
                .collect(Collectors.toList());
    }

    private ReagentUsagePageResponse mapToUsagePageResponse(int page, int size, Page<ReagentHistoryUsage> usageEntities) {
        List<ReagentUsageDetailResponse> list = usageEntities.stream()
                .map(this::mapToUsageDetailResponse)
                .collect(Collectors.toList());

        return ReagentUsagePageResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(usageEntities.getTotalPages())
                .totalElements(usageEntities.getTotalElements())
                .usages(list)
                .build();
    }

    private ReagentUsageDetailResponse mapToUsageDetailResponse(ReagentHistoryUsage usage) {
        return ReagentUsageDetailResponse.builder()
                .usageId(usage.getReagentHistoryUsageId())
                .reagentName(usage.getReagent().getReagentName())
                .quantityUsed(usage.getQuantityUsed())
                .unit(usage.getUnit())
                .usageType(usage.getUsageType())
                .instrumentName(usage.getInstrument() != null ? usage.getInstrument().getInstrumentName() : null)
                .usedAt(usage.getUsedAt())
                .notes(usage.getNotes())
                .build();
    }
}
