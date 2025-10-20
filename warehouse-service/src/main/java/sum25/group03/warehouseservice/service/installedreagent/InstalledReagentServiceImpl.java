package sum25.group03.warehouseservice.service.installedreagent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.repository.InstalledReagentRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstalledReagentServiceImpl implements InstalledReagentService {
    private  final InstalledReagentRepo reagentUsageRepo;
    @Override
    public List<Long> findIdsByInstrumentId(Long instrumentId) {
        return reagentUsageRepo.findIdsByInstrumentId(instrumentId);
    }
}
