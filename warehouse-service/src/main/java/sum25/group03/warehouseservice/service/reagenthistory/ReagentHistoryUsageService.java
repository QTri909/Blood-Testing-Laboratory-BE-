package sum25.group03.warehouseservice.service.reagenthistory;

import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

public interface ReagentHistoryUsageService {
    ReagentUsagePageResponse findAllUsageRecords(String reagentName, String sort, String usageType, Long instrumentId, int page, int size);
    List<ReagentUsageDetailResponse> findByReagentName(String reagentName);
}
