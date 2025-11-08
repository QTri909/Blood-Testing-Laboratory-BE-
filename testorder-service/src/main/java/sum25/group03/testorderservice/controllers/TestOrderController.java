package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.testorderservice.dtos.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dtos.response.CreationTestOrderResponse;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.dtos.request.TestOrderFiltering;
import sum25.group03.testorderservice.dtos.response.TestOrderResponseForInstrument;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.services.interfaces.TestOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-orders")
@RequiredArgsConstructor
@Slf4j
public class TestOrderController {

    private final TestOrderService testOrderService;

    // -------- THUYEN --------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<TestOrderResponseDTO>> getAllTestOrders(
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ApiResponse.add("Get all test orders successfully", testOrderService.getAllTestOrders(viewerId));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TestOrderResponseDTO> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        return ResponseEntity.ok(testOrderService.getTestOrderById(id, viewerId));
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TestOrderResponseDTO>> filterTestOrders(
            @ModelAttribute TestOrderFiltering filterInfo,
            @RequestParam Long viewerId
    ) {
        return ResponseEntity.ok(testOrderService.filterTestOrders(filterInfo, viewerId));
    }

    // -------- HUY -----------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TestOrderResponseDTO> createTestOrder(
            @Valid @RequestBody TestOrderRequestDTO requestDTO,
            @RequestHeader("X-User-Id") Long createdBy
    ) {
        TestOrderResponseDTO response = testOrderService.createTestOrder(requestDTO, createdBy);
        return ApiResponse.add("Test order created successfully", response);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TestOrderResponseDTO> updateTestOrder(
            @PathVariable Long id,
            @Valid @RequestBody TestOrderRequestDTO requestDTO,
            @RequestHeader("X-User-Id") Long updatedBy) {
        log.info("PUT /api/v1/test-orders/{} - Updating test order by user: {}", id, updatedBy);

        TestOrderResponseDTO response = testOrderService.updateTestOrder(id, requestDTO, updatedBy);

        log.info("Test order updated successfully with id: {} by user: {}", id, updatedBy);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTestOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long deletedBy) {
        log.info("DELETE /api/v1/test-orders/{} - Deleting test order by user: {}", id, deletedBy);

        testOrderService.deleteTestOrder(id, deletedBy);

        log.info("Test order deleted successfully with id: {} by user: {}", id, deletedBy);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestOrderResponseDTO>> getTestOrdersByPatientId(
            @PathVariable Long patientId) {
        log.info("GET /api/v1/test-orders/patient/{} - Fetching test orders", patientId);

        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByPatientId(patientId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TestOrderResponseDTO>> getTestOrdersByStatus(
            @PathVariable TestOrderStatus status) {
        log.info("GET /api/v1/test-orders/status/{} - Fetching test orders", status);

        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByStatus(status);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TestOrderResponseDTO> updateTestOrderStatus(
            @PathVariable Long id,
            @RequestParam TestOrderStatus status,
            @RequestHeader("X-User-Id") Long updatedBy) {
        log.info("PATCH /api/v1/test-orders/{}/status - Updating status to {} by user: {}",
                id, status, updatedBy);

        TestOrderResponseDTO response = testOrderService.updateTestOrderStatus(id, status, updatedBy);

        log.info("Test order status updated successfully for id: {} by user: {}", id, updatedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-by/{createdBy}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TestOrderResponseDTO>> getTestOrdersByCreatedBy(
            @PathVariable Long createdBy) {
        log.info("GET /api/v1/test-orders/created-by/{} - Fetching test orders", createdBy);

        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByCreatedBy(createdBy);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/by-barcode/{barcode}")
    public ResponseEntity<TestOrderResponseForInstrument> getByBarcode(
            @PathVariable
            @Pattern(regexp = "^BC-\\d{6}$", message = "Barcode phải có định dạng BC-123456")
            String barcode) {

        TestOrderResponseForInstrument response = testOrderService.findLatestByBarcode(barcode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-unmatched-order")
    public ResponseEntity<CreationTestOrderResponse> createUnmatchedOrder(
            @Valid @RequestParam String barcode) {
        CreationTestOrderResponse response = testOrderService.createTestOrderForExternalSystem(barcode);
        return ResponseEntity.ok(response);
    }
}