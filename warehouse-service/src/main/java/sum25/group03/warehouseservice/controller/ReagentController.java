package sum25.group03.warehouseservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.response.ReagentResponseForInstrument;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.service.reagent.ReagentService;
import sum25.group03.warehouseservice.service.reagentusage.ReagentUsageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reagents")
@RequiredArgsConstructor
@Slf4j

public class ReagentController {
    private final ReagentService reagentService;
    private final ReagentUsageService usageService;

    @GetMapping("/validate/{lotNumber}")
    public ResponseEntity<ReagentValidationResponse> validateReagent(
            @PathVariable String lotNumber,
            @RequestParam Double requiredVolume) {

        log.info("Received reagent validation request for batch number: {} with required volume: {}",
                lotNumber, requiredVolume);

        ReagentValidationResponse response = reagentService.validateReagent(lotNumber, requiredVolume);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReagentResponseForInstrument> > listReagentsForInstrument() {

        List<ReagentResponseForInstrument> response = reagentService.listReagentsForInstrument();

        return ResponseEntity.ok(response);
    }
    @GetMapping("all")
    public ApiResponse<?> getAllReagents() {
        return ApiResponse.ok(reagentService.getAllReagents());
    }

    @GetMapping("/dashboard/usage")
    public ApiResponse<?> getReagentUsageDashboard() {
        return ApiResponse.ok(usageService.getReagentUsageDashboard());
    }

    @GetMapping("listItem")
    public ApiResponse<?> getReagentListItem(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "20") int size
    ) {
        return ApiResponse.ok(reagentService.getReagentListItems(page, size));
    }
    @GetMapping("/{reagentId}")
    public ApiResponse<?> getReagentDetail(
            @PathVariable Long reagentId
    ) {
        return ApiResponse.ok(reagentService.getReagentDetail(reagentId));
    }

    @GetMapping("history")
    public ApiResponse<?> getInstrumentsByReagentId(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "30") int size
    ) {
        return ApiResponse.ok(usageService.getInstrumentsByReagentId(page, size));
    }
}
