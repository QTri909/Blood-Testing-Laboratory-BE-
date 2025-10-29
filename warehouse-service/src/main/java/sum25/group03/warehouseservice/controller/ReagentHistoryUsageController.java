package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.warehouseservice.dto.response.ReagentUsageDetailResponse;
import sum25.group03.warehouseservice.dto.response.ReagentUsagePageResponse;
import sum25.group03.warehouseservice.service.reagenthistory.ReagentHistoryUsageService;

import java.util.List;

@RestController
@RequestMapping("/api/reagents/usage")
@RequiredArgsConstructor
public class ReagentHistoryUsageController {
    private final ReagentHistoryUsageService usageService;

    @GetMapping
    public ResponseEntity<ReagentUsagePageResponse> getUsageRecords(
            @RequestParam(required = false) String reagentName,
            @RequestParam(required = false) String usageType,
            @RequestParam(required = false) Long instrumentId,
            @RequestParam(defaultValue = "usedAt:desc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                usageService.findAllUsageRecords(reagentName, sort, usageType, instrumentId, page, size)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReagentUsageDetailResponse>> getUsageByReagentName(
            @RequestParam String reagentName) {

        List<ReagentUsageDetailResponse> response = usageService.findByReagentName(reagentName);
        return ResponseEntity.ok(response);
    }
}
