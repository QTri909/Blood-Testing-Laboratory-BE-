package sum25.group03.monitoringservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.monitoringservice.dto.PagedResponse;
import sum25.group03.monitoringservice.model.HealthCheckLog;
import sum25.group03.monitoringservice.service.HealthCheckLogService;

@RestController
@RequestMapping("/api/v1/health-logs")
public class HealthCheckLogController {
    private final HealthCheckLogService healthCheckLogService;

    public HealthCheckLogController(HealthCheckLogService healthCheckLogService) {
        this.healthCheckLogService = healthCheckLogService;
    }


    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PagedResponse> getHealthCheckLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<HealthCheckLog> pageResult = healthCheckLogService.getAllLogs(page, size);
        PagedResponse response = PagedResponse.fromPage(pageResult);
        return ApiResponse.add("Fetched health check logs successfully", response);
    }
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @GetMapping("/latest")
    public ApiResponse<?> getLatestHealthCheckLog() {
        return healthCheckLogService.getLatestLog()
                .map(log -> ApiResponse.add("Fetched latest health check log successfully", log))
                .orElse(ApiResponse.error(HttpStatus.NOT_FOUND, "No health check log found", "/api/v1/health-logs/latest"));
    }
}
