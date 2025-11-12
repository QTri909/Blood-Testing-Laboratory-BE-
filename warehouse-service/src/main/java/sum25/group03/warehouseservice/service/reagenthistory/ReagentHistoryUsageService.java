package sum25.group03.warehouseservice.service.reagenthistory;

import org.springframework.data.domain.Pageable;
import sum25.group03.warehouseservice.dto.request.ReagentUsageReq;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.ReagentRes;

public interface ReagentHistoryUsageService {
//    PageRes<ReagentRes> filterReagentsWithUsage(String name, Pageable pageable);
    void useReagent(ReagentUsageReq request);
}
