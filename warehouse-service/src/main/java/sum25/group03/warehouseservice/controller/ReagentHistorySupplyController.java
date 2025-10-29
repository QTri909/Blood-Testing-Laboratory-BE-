package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.ReagentSupplyReq;
import sum25.group03.warehouseservice.service.reagentsupply.ReagentSupplyService;

@RestController
@RequestMapping("/api/v1/supplys")
@RequiredArgsConstructor
public class ReagentHistorySupplyController {
    private final ReagentSupplyService reagentSupplyService;

    @GetMapping("")
    public ResponseEntity<?> getAllReagentSupplyHistory(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(reagentSupplyService.getAll(page,size));
    }
    @PostMapping("")
    public ResponseEntity<?> addReagentSupply(@RequestBody ReagentSupplyReq reagentSupplyReq) {
        reagentSupplyService.addReagentSupply(reagentSupplyReq);
        return ResponseEntity.ok("Reagent supply added successfully.");
    }
}
