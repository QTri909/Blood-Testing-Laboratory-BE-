package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.dto.request.ReagentSupplyReq;
import sum25.group03.warehouseservice.dto.request.UpdateStatusPOReq;
import sum25.group03.warehouseservice.service.reagentsupply.ReagentSupplyService;

@RestController
@RequestMapping("/api/v1/supply")
@RequiredArgsConstructor
public class ReagentHistorySupplyController {
    private final ReagentSupplyService reagentSupplyService;

    @PreAuthorize("hasAuthority('LAB_VIEW') or hasAuthority('SAMPLE_VIEW')")
    @GetMapping("")
    public ApiResponse<?> getAllReagentSupplyHistory(@RequestParam int page, @RequestParam int size) {
        return ApiResponse.ok(reagentSupplyService.getAll(page,size));
    }
    @PreAuthorize("hasAuthority('LAB_UPDATE') or hasAuthority('SAMPLE_RECEIVE')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> addReagentSupply(@RequestBody ReagentSupplyReq reagentSupplyReq) {
        return ApiResponse.ok(reagentSupplyService.addReagentSupply(reagentSupplyReq));
    }

}
