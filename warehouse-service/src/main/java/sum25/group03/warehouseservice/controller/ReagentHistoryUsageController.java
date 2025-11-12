package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.ReagentUsageReq;
import sum25.group03.warehouseservice.dto.response.HistorySupplyRes;
import sum25.group03.warehouseservice.dto.response.HistoryUsageRes;
import sum25.group03.warehouseservice.dto.response.PageRes;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.service.reagentfilter.ReagentFilterService;
import sum25.group03.warehouseservice.service.reagenthistory.ReagentHistoryUsageService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/history")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class ReagentHistoryUsageController {
    private final ReagentHistoryUsageService usageService;
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
            Pageable pageable
    ) {
        return ApiResponse.<PageRes<HistoryUsageRes>>builder()
                .message("Filter usage history successfully")
                .data(filterService.filterUsageHistory(reagentName, startDate, endDate, pageable))
                .build();
    }

    @PostMapping("/use")
    public ApiResponse<String> useReagent(@Valid @RequestBody ReagentUsageReq request) {
        usageService.useReagent(request);
        return ApiResponse.<String>message("Usage recorded successfully and logged.").build();
    }
}
