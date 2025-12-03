package sum25.group03.warehouseservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.ReagentReq;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
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

   // @PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/validate/{lotNumber}")
    public ResponseEntity<ReagentValidationResponse> validateReagent(
            @PathVariable String lotNumber,
            @RequestParam Double requiredVolume) {

        log.info("Received reagent validation request for batch number: {} with required volume: {}",
                lotNumber, requiredVolume);

        ReagentValidationResponse response = reagentService.validateReagent(lotNumber, requiredVolume);

        return ResponseEntity.ok(response);
    }

    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/list")
    public ResponseEntity<List<ReagentResponseForInstrument> > listReagentsForInstrument() {

        List<ReagentResponseForInstrument> response = reagentService.listReagentsForInstrument();

        return ResponseEntity.ok(response);
    }

    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("all")
    public ApiResponse<?> getAllReagents() {
        return ApiResponse.ok(reagentService.getAllReagents());
    }

    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/dashboard/usage")
    public ApiResponse<?> getReagentUsageDashboard() {
        return ApiResponse.ok(usageService.getReagentUsageDashboard());
    }

//    @PreAuthorize("hasAuthority('LAB_VIEW')")
//    @GetMapping("listItem")
//    public ApiResponse<?> getReagentListItem(
//            @RequestParam (defaultValue = "0") int page,
//            @RequestParam (defaultValue = "20") int size
//    ) {
//        return ApiResponse.ok(reagentService.getReagentListItems(page, size));
//    }
    //@PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping("listItem")
    public ApiResponse<?> getReagentListItem() {
        return ApiResponse.ok(reagentService.getReagentListItems());
    }

    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/{reagentId}")
    public ApiResponse<?> getReagentDetail(
            @PathVariable Long reagentId
    ) {
        return ApiResponse.ok(reagentService.getReagentDetail(reagentId));
    }
    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("history")
    public ApiResponse<?> getInstrumentsByReagentId(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "30") int size
    ) {
        return ApiResponse.ok(usageService.getInstrumentsByReagentId(page, size));
    }

    //@PreAuthorize("hasAuthority('LAB_UPDATE') or hasAuthority('SAMPLE_UPDATE') ")
    @PostMapping
    public ApiResponse<?> createReagent(@Valid @RequestBody ReagentReq req ) {
        return ApiResponse.data(reagentService.createReagent(req))
                .message("Reagent created successfully")
                .build();
    }
   // @PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/instrument/{instrumentId}/usage-history")
    public ApiResponse<?> getReagentUsageHistoryByInstrument(
            @PathVariable Long instrumentId,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "20") int size
    ) {
        return ApiResponse.ok(usageService.getReagentUsageHistoryByInstrument(instrumentId, page, size));
    }
   // @PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/reagent/{reagentId}")
    public ApiResponse<?> getReagentById(
            @PathVariable Long reagentId
    ) {
        return ApiResponse.ok(reagentService.getReagentById(reagentId));
    }
    //@PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("/inventory/{reagentId}/lots")
    public ApiResponse<?> getListLotNumberByReagentId(
            @PathVariable Long reagentId
    ) {
        return ApiResponse.ok(reagentService.getListLotNumberByReagentId(reagentId));
    }
}
