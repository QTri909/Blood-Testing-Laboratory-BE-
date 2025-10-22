package sum25.group03.warehouseservice.service.reagentusage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReagentUsageServiceImpl implements ReagentUsageService {
    private final ReagentUsageRepo reagentUsageRepo;
    @Override
    public List<Long> getReagentUsageIdsByInstrumentId(Long instrumentId) {
        return reagentUsageRepo.findAllByInstrument_InstrumentId(instrumentId);
    }
}
