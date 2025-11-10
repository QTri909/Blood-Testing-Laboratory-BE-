package sum25.group03.instrumentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.exception.InsufficientReagentException;
import sum25.group03.instrumentservice.service.RawTestResultService;
import sum25.group03.instrumentservice.service.SimulatorService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/instruments/blood-testing")
@RequiredArgsConstructor
@Slf4j
public class BloodTestingController {

    private final SimulatorService simulatorService;
    private final RawTestResultService rawTestResultService;
    private final AuditLogService auditLogService;

    @PostMapping("/start-test")
    @Operation(summary = "Bắt đầu chạy một xét nghiệm máu",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Yêu cầu chạy xét nghiệm thành công (đang xử lý)",
                            content = @Content(mediaType = "application/json")),

                    @ApiResponse(responseCode = "400",
                            description = "Dữ liệu đầu vào không hợp lệ (Validation failed)"),

                    @ApiResponse(responseCode = "409",
                            description = "Xung đột: Máy không sẵn sàng (Instrument not READY)"),

                    @ApiResponse(responseCode = "503",
                            description = "Lỗi dịch vụ ngoài (Warehouse Service Unavailable)"),

                    @ApiResponse(responseCode = "500",
                            description = "Lỗi hệ thống nội bộ")
            }
    )
    public CompletableFuture<ResponseEntity<RawTestResultResponse>> startBloodTest(
            @RequestBody BloodTestingRequest request,
            HttpServletRequest httpRequest) {

        log.info("Received blood test request for barcode: {} on instrument: {}",
                request.getBarcode(), request.getInstrumentId());

        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return simulatorService.startTest(request)
                .thenApply(result -> {
                    simulatorService.logTestCompletion(request.getBarcode(), ipAddress, userAgent);
                    return ResponseEntity.ok(result);
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof InsufficientReagentException) {
                        simulatorService.logTestFailure(
                                request.getBarcode(),
                                ipAddress,
                                userAgent,
                                "INSUFFICIENT_REAGENT",
                                "Không đủ hóa chất để thực hiện xét nghiệm"
                        );
                        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                                .body(null);
                    }
                    simulatorService.logTestFailure(
                            request.getBarcode(),
                            ipAddress,
                            userAgent,
                            ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "UNKNOWN_ERROR",
                            ex.getMessage()
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
                });
    }

    @DeleteMapping("/result/delete/{resultId}")
    public ResponseEntity<Void> deleteRawTestResult(
            @PathVariable Long resultId,
            HttpServletRequest httpRequest) {
        log.info("Received request to delete RawTestResult with ID: {}", resultId);

        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        rawTestResultService.deleteRawTestResult(resultId, ipAddress, userAgent);
        return ResponseEntity.noContent().build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
