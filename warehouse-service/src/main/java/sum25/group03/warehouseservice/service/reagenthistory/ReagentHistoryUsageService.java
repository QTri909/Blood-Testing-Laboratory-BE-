package sum25.group03.warehouseservice.service.reagenthistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.entity.ReagentHistoryUsage;

import java.util.List;

public interface ReagentHistoryUsageService {
    Page<ReagentRes> filterReagentsWithUsage(String name, Pageable pageable);
    void useReagent(Long reagentId, double quantityUsed, Long userId, String lotNumber);
}
