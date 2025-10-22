package sum25.group03.warehouseservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.service.reagent.ReagentService;

@RestController
@RequestMapping("/api/v1/reagents")
@RequiredArgsConstructor
@Slf4j

public class ReagentController {
    private final ReagentService reagentService;

    @GetMapping("/validate/{batchNumber}")
    public ResponseEntity<ReagentValidationResponse> validateReagent(
            @PathVariable String batchNumber,
            @RequestParam Double requiredVolume) {

        log.info("Received reagent validation request for batch number: {} with required volume: {}",
                batchNumber, requiredVolume);

        ReagentValidationResponse response = reagentService.validateReagent(batchNumber, requiredVolume);

        return ResponseEntity.ok(response);
    }
}
