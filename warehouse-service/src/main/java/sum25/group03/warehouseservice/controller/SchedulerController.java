package sum25.group03.warehouseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.warehouseservice.dto.response.MessageResponse;
import sum25.group03.warehouseservice.entity.enums.OperationalStatus;
import sum25.group03.warehouseservice.service.instrumentStatus.InstrumentStatusService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static sum25.group03.warehouseservice.entity.enums.OperationalStatus.*;

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

    @PostMapping("/cleanup")
    public ResponseEntity<String> triggerCleanup() {
        instrumentStatusService.autoDeleteInactiveInstruments();
        return ResponseEntity.ok("Cleanup job executed manually!");
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable Long id) {
        OperationalStatus status = instrumentStatusService.checkInstrumentStatus(id);
        Map<String, Object> response = new HashMap<>();

        response.put("instrumentId", id);
        response.put("status", status);
        response.put("timestamp", LocalDateTime.now());

        String message = switch (status) {
            case READY -> "Thiết bị đã sẵn sàng.";
            case PROCESSING -> "Thiết bị đang xử lý.";
            case MAINTENANCE -> "Thiết bị đang bảo trì.";
            case ERROR -> "Thiết bị vẫn đang gặp lỗi, vui lòng kiểm tra chi tiết.";
        };
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}
