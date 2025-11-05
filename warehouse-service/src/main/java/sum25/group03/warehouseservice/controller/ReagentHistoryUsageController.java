package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.ReagentUsageReq;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.service.reagenthistory.ReagentHistoryUsageService;

@RestController
@RequestMapping("/api/reagents/history")
@RequiredArgsConstructor
public class ReagentHistoryUsageController {
    private final ReagentHistoryUsageService usageService;

    @GetMapping
    public ApiResponse<Page<ReagentRes>> filterReagentsWithUsage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReagentRes> result = usageService.filterReagentsWithUsage(name, pageable);
        return ApiResponse.ok(result);
    }

    @PostMapping("/use")
    public ApiResponse<String> useReagent(@Valid @RequestBody ReagentUsageReq request) {
        usageService.useReagent(request);
        return ApiResponse.<String>message("Usage recorded successfully and logged.").build();
    }
}
