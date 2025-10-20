package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.repository.InstrumentRepo;
import sum25.group03.warehouseservice.repository.ReagentUsageRepo;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;
    private final InstrumentRepo instrumentRepo;
    private final ReagentUsageRepo reagentUsageRepo;
    @PostMapping("/add")
    public ResponseEntity<?> addInstrument(@RequestBody InstrumentReq instrument) {
        instrumentService.addInstrumentToWarehouse(instrument);
        return ResponseEntity.ok("succcess");
    }
    @GetMapping("/test/{id}")
    public ResponseEntity<?> testEndpoint(@PathVariable Long id) {
        var result = instrumentRepo.findConfigAndReagentByInstrument(id);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/testReagent/{id}")
    public ResponseEntity<?> testEndpointReagent(@PathVariable Long id) {
        return ResponseEntity.ok(reagentUsageRepo.findIdsByInstrumentId(id));
    }
}
