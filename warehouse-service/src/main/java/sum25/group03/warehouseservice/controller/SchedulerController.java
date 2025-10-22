package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.dto.response.MessageResponse;
import sum25.group03.warehouseservice.entity.enums.OperationalStatus;
import sum25.group03.warehouseservice.service.instrumentStatus.InstrumentStatusService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
    private final InstrumentStatusService instrumentStatusService;

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

    @PostMapping("/{id}/delete")
    public ResponseEntity<String> triggerCleanup(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", defaultValue = "system") String username
    ) {
        instrumentStatusService.deleteInstrument(id, username);
        return ResponseEntity.ok("Cleanup job executed manually!");
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<InstrumentStatusResponse> checkStatus(@PathVariable Long id) {
        InstrumentStatusResponse response = instrumentStatusService.checkInstrumentStatus(id);
        return ResponseEntity.ok(response);
    }

    @Scheduled(cron = "0 0 0 * * *") // at 00:00 midnight
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupExpiredInstruments() {
        instrumentStatusService.autoCleanupExpiredInstruments();
        return ResponseEntity.ok("Auto cleanup executed successfully.");
    }
}
