package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
