package sum25.group03.monitoringservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.monitoringservice.dto.EventLogDTO;
import sum25.group03.monitoringservice.dto.PagedResponse;
import sum25.group03.monitoringservice.model.EventLog;
import sum25.group03.monitoringservice.service.EventLogService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
public class EventLogController {

    private final EventLogService eventLogService;

    public EventLogController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }


    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PagedResponse> getEventLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<EventLogDTO> pageResult = eventLogService.getAllEventLogs(page, size);
        PagedResponse response = PagedResponse.fromPage(pageResult);
        return ApiResponse.add("Fetched event logs successfully", response);
    }

    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @GetMapping("/{id}")
    public ApiResponse<?> getEventLogById(@PathVariable String id) {
        return eventLogService.getEventLog(id)
                .map(eventLog -> ApiResponse.add("Fetched event log successfully", eventLog))
                .orElse(ApiResponse.error(HttpStatus.NOT_FOUND, "Event log not found", "/api/v1/logs/" + id));
    }

    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PagedResponse> searchEventLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String sourceService
    ) {
        Page<EventLogDTO> pageResult = eventLogService.searchEventLogs(page, size, action, message, operator, sourceService);
        return ApiResponse.add("Search completed", PagedResponse.fromPage(pageResult));
    }
}
