package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.audit.annotation.SkipAuditLog;
import sum25.group03.warehouseservice.dto.request.AssignConfigAndReagentReq;
import sum25.group03.warehouseservice.dto.request.InstrumentReq;
import sum25.group03.warehouseservice.service.instrument.InstrumentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;

    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @PostMapping("/add")
    public ApiResponse<?> addInstrument(@RequestBody InstrumentReq instrument) {
        return ApiResponse.ok( instrumentService.addInstrumentToWarehouse(instrument));
    }

    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @PutMapping("config-reagents")
    public ApiResponse<?> addConfigAndReagentToInstrument(@RequestBody AssignConfigAndReagentReq req) {
        return ApiResponse.ok(instrumentService.addConfigAndReagentToInstrument(req));
    }

    //@PreAuthorize("hasAuthority('LAB_VIEW')")
    @SkipAuditLog
    @GetMapping("/status/{instrumentId}")
    public ResponseEntity<?> getInstrumentStatus(@PathVariable Long instrumentId) {
        return ResponseEntity.ok(instrumentService.getInstrumentStatus(instrumentId));
    }
    //@PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping("/{instrumentId}")
    public ApiResponse<?> getInstrumentById(@PathVariable Long instrumentId) {
        return ApiResponse.ok(instrumentService.getInstrumentById(instrumentId));
        //return ApiResponse.add("get instrument by id",instrumentService.getInstrumentById(instrumentId));
    }
    //@PreAuthorize("hasAuthority('LAB_VIEW')")
    @GetMapping("all")
    public ApiResponse<?> getAllInstruments(
            @RequestParam(required = false) String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(instrumentService.getAllInstruments(page,size, key));
    }

    @GetMapping("list")
    public ApiResponse<?> getList() {
        return ApiResponse.ok(instrumentService.getList());
    }

    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @DeleteMapping("/{instrumentId}/config")
    public ApiResponse<?> deleteConfig(@PathVariable Long instrumentId) {
        instrumentService.removeConfigFromInstrument(instrumentId);
        return ApiResponse.ok("Configuration removed from instrument successfully");
    }
}
