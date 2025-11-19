package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.response.HistorySupplyRes;
import sum25.group03.warehouseservice.dto.response.HistoryUsageRes;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.service.reagentfilter.ReagentFilterService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/filter")
@RequiredArgsConstructor
public class ReagentFilterController {
    private final ReagentFilterService filterService;

    @GetMapping("/supply")
    public ApiResponse<PageRes<HistorySupplyRes>> filterSupply(
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String reagentName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        return ApiResponse.<PageRes<HistorySupplyRes>>builder()
                .message("Filter usage supply successfully")
                .data(filterService.filterSupplyHistory(vendorName, reagentName, startDate, endDate, pageable))
                .build();
    }

    @GetMapping("/usage")
    public ApiResponse<PageRes<HistoryUsageRes>> filterUsage(
            @RequestParam(required = false) String reagentName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "usedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String direction,
            Pageable pageable
    ) {
        return ApiResponse.<PageRes<HistoryUsageRes>>builder()
                .message("Filter usage history successfully")
                .data(filterService.filterUsageHistory(reagentName, startDate, endDate, sortBy, direction, pageable))
                .build();
    }

}
