package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.audit.annotation.SkipAuditLog;
import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.dto.response.MessageResponse;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;

    @PostMapping("/add")
    public ResponseEntity<?> addInstrument(@RequestBody InstrumentReq instrument) {
        instrumentService.addInstrumentToWarehouse(instrument);
        return ResponseEntity.ok("success");
    }
    @PutMapping("config-reagents")
    public ResponseEntity<?> addConfigAndReagentToInstrument(@RequestBody AssignConfigAndReagentReq req) {
        return ResponseEntity.ok(instrumentService.addConfigAndReagentToInstrument(req));
    }

    @SkipAuditLog
    @GetMapping("/status/{instrumentId}")
    public ResponseEntity<?> getInstrumentStatus(@PathVariable Long instrumentId) {
        return ResponseEntity.ok(instrumentService.getInstrumentStatus(instrumentId));
    }


}
