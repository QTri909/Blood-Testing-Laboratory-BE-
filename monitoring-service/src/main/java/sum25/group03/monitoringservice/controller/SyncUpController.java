package sum25.group03.monitoringservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.monitoringservice.service.SyncUpService;

@RestController
@RequestMapping("/api/v1/admin/sync")
public class SyncUpController {

    private final SyncUpService syncUpService;

    public SyncUpController(SyncUpService syncUpService) {
        this.syncUpService = syncUpService;
    }

    @PostMapping("/manual/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> manualResync(@PathVariable String orderId) {
        boolean success = syncUpService.manualResync(orderId);
        if (success) {
            return ApiResponse.add("Manual re-sync triggered successfully for orderId: " + orderId, orderId);
        }
        return ApiResponse.error(HttpStatus.NOT_FOUND, "No data found for orderId: " + orderId, "/api/admin/sync/manual/" + orderId);
    }
}
