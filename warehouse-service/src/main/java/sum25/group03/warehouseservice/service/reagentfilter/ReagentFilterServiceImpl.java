package sum25.group03.warehouseservice.service.reagentfilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final HistorySupplyRepo historySupplyRepo;
    private final ReagentUsageRepo reagentUsageRepo;
    private final VendorMapper vendorMapper;

    @Override
    public PageRes<HistorySupplyRes> filterSupplyHistory(String vendorName, String reagentName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<ReagentHistorySupply> supplyPage = historySupplyRepo
                .filterSupplyHistory(vendorName, reagentName, startDate, endDate, pageable);

        List<HistorySupplyRes> supplyResList = supplyPage.getContent().stream()
                .map(supply -> HistorySupplyRes.builder()
                        .purchaseOrderNumber(supply.getPurchaseOrderNumber())
                        .vendor(vendorMapper.toDto(supply.getVendor()))
                        .supply(List.of(SupplyRes.builder()
                                        .quantityReceived(supply.getQuantityReceived())
                                        .unitOfMeasurement(supply.getUnitOfMeasurement())
                                        .lotNumber(supply.getLotNumber())
                                        .manufactureDate(supply.getManufactureDate())
                                        .expiryDate(supply.getExpiryDate())
                                        .receivedDate(supply.getReceivedDate())
                                        .receivedBy(supply.getReceivedBy())
                                        .status(supply.getStatus())
                                        .notes(supply.getNotes())
                                        .createdAt(supply.getCreatedAt())
                                        .reagentRes(ReagentRes.builder()
                                                .reagentId(supply.getReagent().getReagentId())
                                                .reagentName(supply.getReagent().getReagentName())
                                                .catalogNumber(supply.getReagent().getCatalogNumber())
                                                .casNumber(supply.getReagent().getCasNumber())
                                                .build())
                                .build()))
                        .build())
                .collect(Collectors.toList());
        return PageRes.<HistorySupplyRes>builder()
                .content(supplyResList)
                .pageNumber(supplyPage.getNumber())
                .pageSize(supplyPage.getSize())
                .totalElements(supplyPage.getTotalElements())
                .totalPages(supplyPage.getTotalPages())
                .build();
    }

    @Override
    public PageRes<HistoryUsageRes> filterUsageHistory(String reagentName, LocalDate startDate, LocalDate endDate, Pageable pageable) {
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
                                .build())
                        .usageType(u.getUsageType())
                        .quantityUsed(u.getQuantityUsed())
                        .unit(u.getUnit())
                        .usedAt(u.getUsedAt())
                        .usedBy(u.getUsedBy())
                        .lotNumber(u.getLotNumber())
                        .notes(u.getNotes())
                        .instrumentId(u.getInstrument().getInstrumentId())
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
