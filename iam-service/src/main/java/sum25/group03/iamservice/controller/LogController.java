package sum25.group03.iamservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.iamservice.dto.response.AuditLogReponse;
import sum25.group03.iamservice.service.Interface.AuditLogService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {
    private final AuditLogService auditLogService;

    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<AuditLogReponse>> getAuditLogs(
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Long entityId) {

        List<AuditLogReponse> logs = auditLogService.getAuditLogs(entityName, entityId);
        return ApiResponse.data(logs)
                .message("Audit logs retrieved successfully")
                .build();
    }

    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportLogs(
            @RequestParam String entityName,
            @RequestParam Long entityId) throws IOException {

        ByteArrayInputStream in = auditLogService.exportLogsAsExcel(entityName, entityId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=audit_logs.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
