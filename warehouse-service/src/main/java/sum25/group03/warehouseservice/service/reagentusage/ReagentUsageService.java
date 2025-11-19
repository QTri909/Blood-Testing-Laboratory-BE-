package sum25.group03.warehouseservice.service.reagentusage;

import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.ReagentDashboardRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageRes;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

public interface ReagentUsageService {
    List<Long> getReagentUsageIdsByInstrumentId(Long instrumentId);
    List<ReagentHistoryUsage> getReagentUsageByInstrument(Long instrumentId);
    ReagentDashboardRes getReagentUsageDashboard();
    PageRes<ReagentUsageRes> getInstrumentsByReagentId(int page, int size);
}
