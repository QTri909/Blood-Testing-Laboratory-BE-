package sum25.group03.warehouseservice.service.reagentusage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.ReagentDashboardRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageRes;
import sum25.group03.warehouseservice.dto.response.TopUsedReagentsRes;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;
import sum25.group03.warehouseservice.repository.ReagentInventoryRepo;
import sum25.group03.warehouseservice.repository.ReagentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReagentUsageServiceImpl implements ReagentUsageService {
    private final ReagentUsageRepo reagentUsageRepo;
    private final ReagentRepo reagentRepo;
    private final ReagentInventoryRepo reagentInventoryRepo;

    @Override
    public List<Long> getReagentUsageIdsByInstrumentId(Long instrumentId) {
        return reagentUsageRepo.findAllByInstrument_InstrumentId(instrumentId);
    }

    @Override
    public List<ReagentHistoryUsage> getReagentUsageByInstrument(Long instrumentId) {
        return reagentUsageRepo.findAllByInstrument(instrumentId);
    }

    @Override
    public ReagentDashboardRes getReagentUsageDashboard() {
        // total distinct reagents available in system
        int totalReagents = reagentRepo.findAllDistinct().size();

        // low stock reagents (repository returns Long) -> convert to int safely
        Long lowStockReagentsLong = reagentInventoryRepo.countLowStockReagents();
        int lowStockReagents = lowStockReagentsLong == null ? 0 : lowStockReagentsLong.intValue();

        // expiring lots within next 30 days (repository returns Long) -> convert to int
        LocalDate today = LocalDate.now();
        LocalDate dateLimit = today.plusDays(30);
        Long expiringSoonLotsLong = reagentInventoryRepo.countExpiringLots(dateLimit);
        int expiringSoonLots = expiringSoonLotsLong == null ? 0 : expiringSoonLotsLong.intValue();

        // today's usage
        Double todayUsage = reagentUsageRepo.getTotalUsageByDate(today);
        if (todayUsage == null) todayUsage = 0.0;

        // top used reagents (top 5)
        List<Object[]> topUsed = reagentUsageRepo.findTopUsedReagents(PageRequest.of(0, 5));
        List<TopUsedReagentsRes> topUsedRes = topUsed.stream().map(arr -> {
            String name = (String) arr[0];
            Number total = (Number) arr[1];
            return TopUsedReagentsRes.builder()
                    .reagentName(name)
                    .totalUsed(total == null ? 0 : total.intValue())
                    .build();
        }).collect(Collectors.toList());

        return ReagentDashboardRes.builder()
                .totalReagents(totalReagents)
                .lowStockReagents(lowStockReagents)
                .expiringSoonLots(expiringSoonLots)
                .todayUsage(todayUsage)
                .topUsedReagents(topUsedRes)
                .build();
    }

    @Override
    public PageRes<ReagentUsageRes> getInstrumentsByReagentId(int page, int size) {
        Page<ReagentHistoryUsage> usagesPage = reagentUsageRepo.findAllUsage(PageRequest.of(page, size));

        List<ReagentUsageRes> content = usagesPage.stream().map(u -> ReagentUsageRes.builder()
                .useDate(u.getUsedAt())
                .instrumentId(u.getInstrument() != null ? u.getInstrument().getInstrumentId() : null)
                .instrumentName(u.getInstrument() != null ? u.getInstrument().getInstrumentName() : null)
                .quantityUsed(u.getQuantityUsed() == null ? 0.0 : u.getQuantityUsed())
                .lotNumber(u.getLotNumber())
                .build()).collect(Collectors.toList());

        return PageRes.<ReagentUsageRes>builder()
                .content(content)
                .pageNumber(usagesPage.getNumber())
                .pageSize(usagesPage.getSize())
                .totalElements(usagesPage.getTotalElements())
                .totalPages(usagesPage.getTotalPages())
                .build();
    }
}
