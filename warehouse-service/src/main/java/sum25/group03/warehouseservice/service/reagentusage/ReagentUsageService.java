package sum25.group03.warehouseservice.service.reagentusage;

import java.util.List;

public interface ReagentUsageService {
    List<Long> findIdsByInstrumentId(Long instrumentId);
}
