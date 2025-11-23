package sum25.group03.warehouseservice.service.reagentsupply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.dto.request.ReagentSupplyReq;
import sum25.group03.warehouseservice.dto.request.SupplyReq;
import sum25.group03.warehouseservice.dto.request.UpdateStatusPOReq;
import sum25.group03.warehouseservice.dto.response.*;
import sum25.group03.warehouseservice.entity.ReagentHistorySupply;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;
import sum25.group03.warehouseservice.entity.Vendors;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.VendorMapper;
import sum25.group03.warehouseservice.repository.HistorySupplyRepo;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.VendorRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagentSupplyServiceImpl implements  ReagentSupplyService {
    private final HistorySupplyRepo historySupplyRepo;
    private final VendorMapper vendorMapper;
    private final VendorRepo vendorRepo;
    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;

    @Override
    public PageRes<HistorySupplyRes> getAll(int page, int size) {
        Page<UUID> batchCodes = historySupplyRepo.findDistinctBatchCode(PageRequest.of(page, size));
        List<ReagentHistorySupply> historyList =
                historySupplyRepo.findAllByBatchCodeInFetch(batchCodes.getContent());
        //Page<ReagentHistorySupply> historySupplies = historySupplyRepo.findAllWithVendorAndReagent((PageRequest.of(page, size)));
        //List<ReagentHistorySupply> historyList = historySupplies.getContent();
        Map<UUID, List<ReagentHistorySupply>> groupedByBatchCodes =
                historyList.stream().collect(Collectors.groupingBy(ReagentHistorySupply::getBatchCode));

        List<HistorySupplyRes> historySupplyResList = groupedByBatchCodes.entrySet().stream()
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
            UUID batchCode = entry.getKey();
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
                        .unitOfMeasurement(hs.getUnitOfMeasurement().getUnit())
                        .receivedBy(hs.getReceivedBy())
                        .expiryDate(hs.getExpiryDate())
                        .manufactureDate(hs.getManufactureDate())
                        .notes(hs.getNotes())
                        .createdAt(hs.getCreatedAt())
                        .reagentRes(reagentRes)
                        .build();
            }).toList();

            return new HistorySupplyRes(batchCode,vendorRes, supplyResList);
        }).toList();
        return PageRes.<HistorySupplyRes>builder()
                .content(historySupplyResList)
                .pageNumber(batchCodes.getNumber())
                .pageSize(batchCodes.getSize())
                .totalElements(batchCodes.getTotalElements())
                .totalPages(batchCodes.getTotalPages())
                .build();
    }

    @Transactional
    @Override
    public HistorySupplyRes addReagentSupply(ReagentSupplyReq reagentSupplyReq) {

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

        UUID batchCode = UUID.randomUUID();
        List<ReagentHistorySupply> supplies = reagentSupplyReq.getSupplyReq().stream()
                .map(supplyReq -> {
                    Reagents reagent = reagentMap.get(supplyReq.getReagentId());
                    if (reagent == null) {
                        throw new NotFoundException("Reagent not found: " + supplyReq.getReagentId());
                    }
                    return ReagentHistorySupply.builder()
                            .batchCode(batchCode)
                            .lotNumber(supplyReq.getLotNumber())
                            .manufactureDate(supplyReq.getManufactureDate())
                            .expiryDate(supplyReq.getExpiryDate())
                            .quantityReceived(supplyReq.getQuantityReceived())
                            .unitOfMeasurement(supplyReq.getUnitOfMeasurement())
                            .notes(supplyReq.getNotes()!=null? supplyReq.getNotes() : "")
                            .reagent(reagent)
                            .vendor(vendor)
                            .build();
                })
                .toList();

        List<ReagentHistorySupply> saveSupplies = historySupplyRepo.saveAll(supplies);

        List<ReagentInventory> newInventories = saveSupplies.stream().map(s -> {
            ReagentInventory inventory = new ReagentInventory();
            inventory.setReagent(s.getReagent());
            inventory.setLotNumber(s.getLotNumber());
            inventory.setQuantityAvailable(s.getQuantityReceived());
            inventory.setExpiryDate(s.getExpiryDate());
            inventory.setStatus(ReagentInventoryStatus.AVAILABLE);
            return inventory;
        }).toList();
        reagentInventoryRepo.saveAll(newInventories);
        log.info("Added {} reagent supplies for batchCode: {}", supplies.size(), batchCode);
        VendorRes vendorRes = vendorMapper.toDto(vendor);
        List<SupplyRes> supplyResList = saveSupplies.stream().map(hs -> {
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
                    .unitOfMeasurement(hs.getUnitOfMeasurement().getUnit())
                    .receivedBy(hs.getReceivedBy())
                    .expiryDate(hs.getExpiryDate())
                    .manufactureDate(hs.getManufactureDate())
                    .notes(hs.getNotes()!=null? hs.getNotes() : "")
                    .createdAt(hs.getCreatedAt())
                    .reagentRes(reagentRes)
                    .build();
        }).toList();
        return HistorySupplyRes.builder()
                .batchCode(batchCode)
                .vendor(vendorRes)
                .supply(supplyResList)
                .build();
    }

//    @Transactional
//    @Override
//    public void updateReagentSupplyStatus(UpdateStatusPOReq req) {
//        List<ReagentHistorySupply> supplies = historySupplyRepo.findAllByPurchaseOrderNumber(req.getPurchaseOrderNumber());
//        supplies.forEach(s -> s.setStatus(req.getSupplyStatus()));
//        if (req.getSupplyStatus() == SupplyStatus.RECEIVED) {
//            List<ReagentInventory> newInventories = supplies.stream().map(s -> {
//                ReagentInventory inventory = new ReagentInventory();
//                inventory.setReagent(s.getReagent());
//                inventory.setLotNumber(s.getLotNumber());
//                inventory.setQuantityAvailable(s.getQuantityReceived());
//                inventory.setExpiryDate(s.getExpiryDate());
//                inventory.setStatus(ReagentInventoryStatus.AVAILABLE);
//                return inventory;
//            }).toList();
//            reagentInventoryRepo.saveAll(newInventories);
//        }
//        historySupplyRepo.saveAll(supplies);
//    }
}