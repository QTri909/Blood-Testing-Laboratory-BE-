package sum25.group03.warehouseservice.service.reagentfilter;

import org.springframework.data.domain.Pageable;
import sum25.group03.warehouseservice.dto.response.HistorySupplyRes;
import sum25.group03.warehouseservice.dto.response.HistoryUsageRes;
import sum25.group03.warehouseservice.dto.response.PageRes;

public interface ReagentFilterService {
//    public PageRes<HistorySupplyRes> filterSupplyHistory(
//            String vendorName,
//            String reagentName,
//            java.time.LocalDate startDate,
//            java.time.LocalDate endDate,
//            Pageable pageable
//    );

    public PageRes<HistoryUsageRes> filterUsageHistory(
            String reagentName,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            String sortBy,
            String direction,
            Pageable pageable
    );
}
