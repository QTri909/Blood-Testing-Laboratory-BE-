package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sum25.group03.warehouseservice.service.instrumentCleanup.InstrumentCleanupScheduler;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
public class SchedulerController {
    private final InstrumentCleanupScheduler cleanupScheduler;

    @PostMapping("/cleanup")
    public ResponseEntity<String> triggerCleanup() {
        cleanupScheduler.autoDeleteInactiveInstruments();
        return ResponseEntity.ok("Cleanup job executed manually!");
    }
}
