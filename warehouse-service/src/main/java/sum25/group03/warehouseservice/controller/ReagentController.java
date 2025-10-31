package sum25.group03.warehouseservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.ReagentResponseForInstrument;
import sum25.group03.warehouseservice.dto.response.ReagentValidationResponse;
import sum25.group03.warehouseservice.service.reagent.ReagentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reagents")
@RequiredArgsConstructor
@Slf4j

public class ReagentController {
    private final ReagentService reagentService;

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
}
