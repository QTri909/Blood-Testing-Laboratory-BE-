package sum25.group03.warehouseservice.service.reagentsupply;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.Vendors;
import sum25.group03.warehouseservice.mapper.VendorMapper;
import sum25.group03.warehouseservice.repository.HistorySupplyRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReagentSupplyServiceImpl implements  ReagentSupplyService {
    private final HistorySupplyRepo historySupplyRepo;
    private final VendorMapper vendorMapper;

    @Override
    public PageRes<HistorySupplyRes> getAll(int page, int size) {
        Page<ReagentHistorySupply> historySupplies  = historySupplyRepo.findAllWithVendorAndReagent((PageRequest.of(page, size)));
        List<ReagentHistorySupply> historyList = historySupplies.getContent();
        Map<Vendors, List<ReagentHistorySupply>> groupedByVendor =
                historyList.stream().collect(Collectors.groupingBy(ReagentHistorySupply::getVendor));

        List<HistorySupplyRes> historySupplyResList = groupedByVendor.entrySet().stream()
                .sorted((e1, e2) -> {
                    LocalDateTime latest1 = e1.getValue().stream()
                            .map(ReagentHistorySupply::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);
                    LocalDateTime latest2 = e2.getValue().stream()
                            .map(ReagentHistorySupply::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);
                    return latest2.compareTo(latest1); // DESC
                })
                .map(entry -> {
            Vendors vendor = entry.getKey();
            List<ReagentHistorySupply> supplies = entry.getValue();

            VendorRes vendorRes = vendorMapper.toDto(vendor);

            List<SupplyRes> supplyResList = supplies.stream().map(hs -> {
                Reagents r = hs.getReagent();
                ReagentRes reagentRes = ReagentRes.builder()
                        .reagentId(r.getReagentId())
                        .reagentName(r.getReagentName())
                        .catalogNumber(r.getCatalogNumber())
                        .casNumber(r.getCasNumber())
                        .build();

                return SupplyRes.builder()
                        .purchaseOrderNumber(hs.getPurchaseOrderNumber())
                        .quantityReceived(hs.getQuantityReceived())
                        .lotNumber(hs.getLotNumber())
                        .unitOfMeasurement(hs.getUnitOfMeasurement())
                        .receivedDate(hs.getReceivedDate())
                        .receivedBy(hs.getReceivedBy())
                        .expiryDate(hs.getExpiryDate())
                        .manufactureDate(hs.getManufactureDate())
                        .status(hs.getStatus())
                        .notes(hs.getNotes())
                        .createdAt(hs.getCreatedAt())
                        .reagentRes(reagentRes)
                        .build();
            }).toList();

            return new HistorySupplyRes(vendorRes, supplyResList);
        }).toList();
        return PageRes.<HistorySupplyRes>builder()
                .content(historySupplyResList)
                .pageNumber(historySupplies.getNumber())
                .pageSize(historySupplies.getSize())
                .totalElements(historySupplies.getTotalElements())
                .totalPages(historySupplies.getTotalPages())
                .build();
    }
}
