package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.MessageResponse;
import sum25.group03.warehouseservice.service.instrumentcleanup.InstrumentCleanupService;
import sum25.group03.warehouseservice.service.instrumentstatus.InstrumentStatusService;

@RestController
@RequestMapping("/api/v1/instruments")
@RequiredArgsConstructor
public class InstrumentStatusController {
    private final InstrumentStatusService instrumentStatusService;
    private final InstrumentCleanupService instrumentCleanupService;

    @PutMapping("/{id}/activate")
    public ResponseEntity<MessageResponse> activateInstrument(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        instrumentStatusService.activateInstrument(id, username);
        return ResponseEntity.ok(new MessageResponse("Instrument activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<MessageResponse> deactivateInstrument(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        instrumentStatusService.deactivateInstrument(id, username);
        return ResponseEntity.ok(new MessageResponse("Instrument deactivated successfully"));
    }

//    @PostMapping("/delete/{id}")
//    public ResponseEntity<String> triggerCleanup(
//            @PathVariable Long id,
//            @RequestHeader(value = "X-User", defaultValue = "system") String username
//    ) {
//        instrumentStatusService.deleteInstrument(id, username);
//        return ResponseEntity.ok("Cleanup job executed manually!");
//    }
//
//    @PostMapping("/cleanup")
//    public ResponseEntity<String> triggerCleanup() {
//        instrumentCleanupService.autoDeleteInactiveInstruments();
//        return ResponseEntity.ok("Manual cleanup executed successfully.");
//    }
}
