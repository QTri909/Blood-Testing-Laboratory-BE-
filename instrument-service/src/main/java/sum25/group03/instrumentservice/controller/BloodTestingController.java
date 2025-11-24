package sum25.group03.instrumentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.common.response.dtos.grpc.CleanTestOrderResponse;
import sum25.group03.instrumentservice.audit.service.AuditLogService;
import sum25.group03.instrumentservice.client.TestOrderServiceClient;
import sum25.group03.instrumentservice.client.response.TestOrderResponse;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.exception.BarcodeAlreadyTestedException;
import sum25.group03.instrumentservice.exception.InstrumentNotReadyException;
import sum25.group03.instrumentservice.exception.InsufficientReagentException;
import sum25.group03.instrumentservice.service.BloodTestingService;
import sum25.group03.instrumentservice.service.RawTestResultService;
import sum25.group03.instrumentservice.service.SimulatorService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/v1/instruments/blood-testing")
@RequiredArgsConstructor
@Slf4j
public class BloodTestingController {

    private final SimulatorService simulatorService;
    private final RawTestResultService rawTestResultService;
    private final BloodTestingService bloodTestingService;

    private final TestOrderServiceClient testOrderServiceClient;

    @PostMapping("/start-test")
    @Operation(summary = "Bắt đầu chạy một xét nghiệm máu"
    )
    public CompletableFuture<ResponseEntity<Object>> startBloodTest(
            @RequestBody BloodTestingRequest request,
            HttpServletRequest httpRequest) {

        log.info("Received blood test request for barcode: {} on instrument: {}",
                request.getBarcode(), request.getInstrumentId());

        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        return simulatorService.startTest(request)
                .thenApply(result -> {
                    simulatorService.logTestCompletion(request.getBarcode(), ipAddress, userAgent);
                    return ResponseEntity.ok((Object) result);
                })
                .exceptionally(ex -> {
                    Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    String errorMessage = (cause != null) ? cause.getMessage() : ex.getMessage();
                    String errorType = (cause != null) ? cause.getClass().getSimpleName() : "UNKNOWN_ERROR";
                    simulatorService.logTestFailure(
                            request.getBarcode(),
                            ipAddress,
                            userAgent,
                            errorType,
                            errorMessage
                    );


                    Map<String, Object> errorBody = new HashMap<>();
                    errorBody.put("timestamp", new Date());
                    errorBody.put("path", httpRequest.getRequestURI());

                    if (cause instanceof BarcodeAlreadyTestedException) {
                        errorBody.put("status", HttpStatus.CONFLICT.value());
                        errorBody.put("error", "Conflict");
                        errorBody.put("message", errorMessage);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
                    }

                    if (cause instanceof InstrumentNotReadyException) {
                        errorBody.put("status", HttpStatus.CONFLICT.value());
                        errorBody.put("error", "Conflict");
                        errorBody.put("message", errorMessage);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
                    }

                    if (cause instanceof InsufficientReagentException) {
                        errorBody.put("status", HttpStatus.PRECONDITION_FAILED.value());
                        errorBody.put("error", "Precondition Failed");
                        errorBody.put("message", errorMessage);
                        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(errorBody);
                    }

                    if (cause instanceof RuntimeException && errorMessage != null) {
                        if (errorMessage.contains("Invalid barcode format") ||
                                errorMessage.contains("Barcode is null or empty")) {

                            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
                            errorBody.put("error", "Bad Request");
                            errorBody.put("message", errorMessage);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
                        }
                    }

                    log.error("Unhandled Async Error: ", cause); // Log stack trace
                    errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    errorBody.put("error", "Internal Server Error");
                    errorBody.put("message", "An unexpected error occurred. Please try again later.");

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
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

    @GetMapping("/test-order/{id}")
    @ResponseStatus(HttpStatus.OK)
    // @Operation
    public ApiResponse<CleanTestOrderResponse> getCleanTestOrderById(
            @PathVariable Long id
    ) {
        CleanTestOrderResponse response = bloodTestingService.getCleanTestOrderById(id);
        return ApiResponse.add(
                "Fetch clean test order by ID successful",
                response
        );
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public Object testEndpoint() {

        TestOrderResponse testOrder = testOrderServiceClient.getTestOrderByBarcode("BC-394883");

        return testOrder;
    }

}
