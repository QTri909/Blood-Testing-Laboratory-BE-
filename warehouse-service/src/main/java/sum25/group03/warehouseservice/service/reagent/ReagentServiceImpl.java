package sum25.group03.warehouseservice.service.reagent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.group03.warehouseservice.repository.ReagentRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReagentServiceImpl implements ReagentService {
    private final ReagentRepo reagentRepo;
    @Override
    public List<Long> findExistingIds(List<Long> reagentIds) {
        return reagentRepo.findExistingIds(reagentIds);
    }
}
