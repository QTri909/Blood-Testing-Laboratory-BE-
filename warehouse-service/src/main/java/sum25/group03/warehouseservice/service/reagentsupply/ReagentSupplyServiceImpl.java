package sum25.group03.warehouseservice.service.reagentsupply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.request.ReagentSupplyReq;
import sum25.group03.warehouseservice.dto.request.SupplyReq;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.Vendors;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.VendorMapper;
import sum25.group03.warehouseservice.repository.HistorySupplyRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.VendorRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentSupplyServiceImpl implements  ReagentSupplyService {
    private final HistorySupplyRepo historySupplyRepo;
    private final VendorMapper vendorMapper;
    private final VendorRepo vendorRepo;
    private final ReagentRepo reagentRepo;

    @Override
    public PageRes<HistorySupplyRes> getAll(int page, int size) {
        Page<String> purchaseOrders = historySupplyRepo.findDistinctPurchaseOrderNumbers(PageRequest.of(page, size));
        List<ReagentHistorySupply> historyList =
                historySupplyRepo.findAllByPurchaseOrderNumberInFetch(purchaseOrders.getContent());
        //Page<ReagentHistorySupply> historySupplies = historySupplyRepo.findAllWithVendorAndReagent((PageRequest.of(page, size)));
        //List<ReagentHistorySupply> historyList = historySupplies.getContent();
        Map<String, List<ReagentHistorySupply>> groupedByPO =
                historyList.stream().collect(Collectors.groupingBy(ReagentHistorySupply::getPurchaseOrderNumber));

        List<HistorySupplyRes> historySupplyResList = groupedByPO.entrySet().stream()
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
            String poNumber = entry.getKey();
            List<ReagentHistorySupply> supplies = entry.getValue();

            Vendors vendor = supplies.get(0).getVendor();
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

            return new HistorySupplyRes(poNumber,vendorRes, supplyResList);
        }).toList();
        return PageRes.<HistorySupplyRes>builder()
                .content(historySupplyResList)
                .pageNumber(purchaseOrders.getNumber())
                .pageSize(purchaseOrders.getSize())
                .totalElements(purchaseOrders.getTotalElements())
                .totalPages(purchaseOrders.getTotalPages())
                .build();
    }

    @Override
    public void addReagentSupply(ReagentSupplyReq reagentSupplyReq) {

        reagentSupplyReq.getSupplyReq().forEach(r -> {
            if (r.getExpiryDate().isBefore(r.getManufactureDate())) {
                throw new IllegalArgumentException(String.format(
                        "Expiry date (%s) cannot be before manufacture date (%s) for reagentId=%d",
                        r.getExpiryDate(), r.getManufactureDate(), r.getReagentId()
                ));
            }
        });

        Vendors vendor = vendorRepo.findById(reagentSupplyReq.getVendorId())
                .orElseThrow(() -> new NotFoundException("Vendor not found"));
        List<Long> reagentIds = reagentSupplyReq.getSupplyReq().stream()
                .map(SupplyReq::getReagentId)
                .toList();
        Map<Long, Reagents> reagentMap = reagentRepo.findAllById(reagentIds).stream()
                .collect(Collectors.toMap(Reagents::getReagentId, r -> r));

        List<ReagentHistorySupply> supplies = reagentSupplyReq.getSupplyReq().stream()
                .map(supplyReq -> {
                    Reagents reagent = reagentMap.get(supplyReq.getReagentId());
                    if (reagent == null) {
                        throw new NotFoundException("Reagent not found: " + supplyReq.getReagentId());
                    }
                    return ReagentHistorySupply.builder()
                            .purchaseOrderNumber(reagentSupplyReq.getPurchaseOrderNumber())
                            .lotNumber(supplyReq.getLotNumber())
                            .manufactureDate(supplyReq.getManufactureDate())
                            .expiryDate(supplyReq.getExpiryDate())
                            .quantityReceived(supplyReq.getQuantityReceived())
                            .unitOfMeasurement(supplyReq.getUnitOfMeasurement())
                            .status(SupplyStatus.PENDING)
                            .notes(supplyReq.getNotes()!=null? supplyReq.getNotes() : "")
                            .reagent(reagent)
                            .vendor(vendor)
                            .build();
                })
                .toList();

        historySupplyRepo.saveAll(supplies);
        log.info("Added {} reagent supplies for PO: {}", supplies.size(), reagentSupplyReq.getPurchaseOrderNumber());
    }
}
