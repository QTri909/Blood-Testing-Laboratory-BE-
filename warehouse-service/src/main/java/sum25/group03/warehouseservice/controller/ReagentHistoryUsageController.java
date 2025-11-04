package sum25.group03.warehouseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.ReagentUsageReq;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.service.reagenthistory.ReagentHistoryUsageService;
import sum25.group03.warehouseservice.service.reagentinventory.ReagentInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/reagents/history")
@RequiredArgsConstructor
public class ReagentHistoryUsageController {
    private final ReagentHistoryUsageService usageService;

    @GetMapping
    public ResponseEntity<Page<ReagentRes>> filterReagentsWithUsage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReagentRes> result = usageService.filterReagentsWithUsage(name, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/use")
    public ResponseEntity<String> useReagent(@Valid @RequestBody ReagentUsageReq request) {
        usageService.useReagent(request);
        return ResponseEntity.ok("Usage recorded successfully and logged.");
    }
}
