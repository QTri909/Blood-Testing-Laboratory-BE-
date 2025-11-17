package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("")
    public ApiResponse<?> getAllReagentSupplyHistory(@RequestParam int page, @RequestParam int size) {
        return ApiResponse.ok(reagentSupplyService.getAll(page,size));
    }
//    @PostMapping("")
//    public ResponseEntity<?> addReagentSupply(@RequestBody ReagentSupplyReq reagentSupplyReq) {
//        reagentSupplyService.addReagentSupply(reagentSupplyReq);
//        return ResponseEntity.ok("Reagent supply added successfully.");
//    }
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> addReagentSupply(@RequestBody ReagentSupplyReq reagentSupplyReq) {
        return ApiResponse.ok(reagentSupplyService.addReagentSupply(reagentSupplyReq));
    }
//    @PatchMapping("")
//    public ResponseEntity<?> updateReagentSupplyStatus(@RequestBody UpdateStatusPOReq req) {
//        reagentSupplyService.updateReagentSupplyStatus(req);
//        return ResponseEntity.ok("Reagent supply status updated successfully.");
//    }
//    @PatchMapping("")
//    public ApiResponse<?> updateReagentSupplyStatus(@RequestBody UpdateStatusPOReq req) {
//        return ApiResponse.oke(reagentSupplyService.updateReagentSupplyStatus(req));
//    }
}
