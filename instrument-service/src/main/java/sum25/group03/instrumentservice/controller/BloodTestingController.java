package sum25.group03.instrumentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.instrumentservice.controller.request.BloodTestingRequest;
import sum25.group03.instrumentservice.controller.response.RawTestResultResponse;
import sum25.group03.instrumentservice.exception.InsufficientReagentException;
import sum25.group03.instrumentservice.service.SimulatorService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/blood-testing")
@RequiredArgsConstructor
@Slf4j
public class BloodTestingController {

    private final SimulatorService simulatorService;

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
            @RequestBody BloodTestingRequest request) {

        log.info("Received blood test request for barcode: {} on instrument: {}",
                request.getBarcode(), request.getInstrumentId());

        return simulatorService.startTest(request)
                .thenApply(result -> {
                    log.info("Blood test completed successfully for barcode: {}", request.getBarcode());
                    return ResponseEntity.ok(result);
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof InsufficientReagentException) {
                        log.warn("Insufficient reagent for barcode: {}", request.getBarcode());
                        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                                .body(null);
                    }
                    log.error("Error during blood test for barcode: {}", request.getBarcode(), ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
                });
    }


    @GetMapping("/result/{resultId}")
    public ResponseEntity<String> getTestResult(@PathVariable Integer resultId) {
        log.info("Fetching test result with ID: {}", resultId);
        return ResponseEntity.ok("Test result endpoint");
    }
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Blood Testing Service is running");
    }
}
