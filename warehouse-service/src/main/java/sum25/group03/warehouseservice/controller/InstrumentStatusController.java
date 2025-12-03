package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.warehouseservice.service.instrumentcleanup.InstrumentCleanupService;
import sum25.group03.warehouseservice.service.instrumentstatus.InstrumentStatusService;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class
InstrumentStatusController {
    private final InstrumentStatusService instrumentStatusService;
    private final InstrumentCleanupService instrumentCleanupService;

    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @PutMapping("/{id}/activate")
    public ApiResponse<String> activateInstrument(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        instrumentStatusService.activateInstrument(id, username);
        return ApiResponse.<String>message("Instrument activated successfully").build();
    }
    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @PutMapping("/{id}/deactivate")
    public ApiResponse<String> deactivateInstrument(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        instrumentStatusService.deactivateInstrument(id, username);
        return ApiResponse.<String>message("Instrument deactivated successfully").build();
    }
    //@PreAuthorize("hasAuthority('LAB_UPDATE')")
    @PostMapping("/test-auto-delete")
    public ApiResponse<String> testAutoDeleteInactiveInstruments() {
        instrumentCleanupService.autoDeleteInactiveInstruments();
        return ApiResponse.<String>message("Auto delete task executed manually").build();
    }
}
