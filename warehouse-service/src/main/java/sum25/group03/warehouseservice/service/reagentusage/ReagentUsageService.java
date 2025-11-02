package sum25.group03.warehouseservice.service.reagentusage;

import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

public interface ReagentUsageService {
    List<Long> getReagentUsageIdsByInstrumentId(Long instrumentId);
    List<ReagentHistoryUsage> getReagentUsageByInstrument(Long instrumentId);
}
