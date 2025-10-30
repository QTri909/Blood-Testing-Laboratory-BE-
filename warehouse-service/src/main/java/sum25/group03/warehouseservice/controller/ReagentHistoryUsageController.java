package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.service.reagenthistory.ReagentHistoryUsageService;

import java.util.List;

@RestController
@RequestMapping("/api/reagents/usage")
@RequiredArgsConstructor
public class ReagentHistoryUsageController {
    private final ReagentHistoryUsageService usageService;

    @GetMapping("/filter")
    public ResponseEntity<Page<ReagentRes>> filterReagentsWithUsage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReagentRes> result = usageService.filterReagentsWithUsage(name, pageable);
        return ResponseEntity.ok(result);
    }
}
