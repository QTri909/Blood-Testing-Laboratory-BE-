package sum25.group03.instrumentservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sum25.group03.instrumentservice.controller.response.PageRes;
import sum25.group03.instrumentservice.controller.response.ReagentHistoryUsageOfInstrumentRes;
import sum25.group03.instrumentservice.model.ReagentHistoryUsage;
import sum25.group03.instrumentservice.repository.ReagentHistoryUsageRepository;
import sum25.group03.instrumentservice.service.UsageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    private final ReagentHistoryUsageRepository reagentUsageRepo;

    @Override
    public PageRes<ReagentHistoryUsageOfInstrumentRes> getReagentUsageHistoryByInstrument(Long instrumentId, int page, int size) {
        Page<ReagentHistoryUsage> pageReagentUsage = reagentUsageRepo.findAllByInstrument_InstrumentId(instrumentId, PageRequest.of(page, size));
        List<ReagentHistoryUsageOfInstrumentRes> content = pageReagentUsage.stream().map(ru -> ReagentHistoryUsageOfInstrumentRes.builder()
                .reagentName(ru.getReagentName())
                .lotNumber(ru.getLotNumber())
                .usedAt(ru.getUsedAt())
                .quantityUsed(ru.getVolumeUsed())
                .unit(ru.getUnit())
                .build()).toList();
        return PageRes.<ReagentHistoryUsageOfInstrumentRes>builder()
                .content(content)
                .pageNumber(pageReagentUsage.getNumber())
                .totalPages(pageReagentUsage.getTotalPages())
                .totalElements(pageReagentUsage.getTotalElements())
                .pageSize(pageReagentUsage.getSize())
                .build();
    }
}
