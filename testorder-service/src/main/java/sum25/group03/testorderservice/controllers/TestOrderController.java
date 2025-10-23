package sum25.group03.testorderservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.dto.response.TestOrderResponseDTO;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.service.interfaces.TestOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-orders")
@RequiredArgsConstructor
@Slf4j
public class TestOrderController {

    private final TestOrderService testOrderService;

    // -------- THUYEN --------
    @GetMapping
    public ResponseEntity<List<TestOrderResponse>> getAllTestOrders(@RequestParam Long viewerId) {
        return ResponseEntity.ok(testOrderService.getAllTestOrders(viewerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponse> getById(@PathVariable Long id, @RequestParam Long viewerId) {
        return ResponseEntity.ok(testOrderService.getTestOrderById(id, viewerId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TestOrderResponse>> filterTestOrders(
            @ModelAttribute TestOrderFiltering filterInfo,
            @RequestParam Long viewerId
    ) {
        return ResponseEntity.ok(testOrderService.filterTestOrders(filterInfo, viewerId));
    }

    // -------- HUY -----------
    @PostMapping
    public ResponseEntity<TestOrderResponseDTO> createTestOrder(
            @Valid @RequestBody TestOrderRequestDTO requestDTO) {
        log.info("POST /api/v1/test-orders - Creating test order for patientId: {}",
                requestDTO.getPatientId());

        TestOrderResponseDTO response = testOrderService.createTestOrder(requestDTO);

        log.info("Test order created successfully with id: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<List<TestOrderResponseDTO>> getTestOrdersByStatus(
            @PathVariable TestOrderStatus status) {
        log.info("GET /api/v1/test-orders/status/{} - Fetching test orders", status);

        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByStatus(status);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
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
    public ResponseEntity<List<TestOrderResponseDTO>> getTestOrdersByCreatedBy(
            @PathVariable Long createdBy) {
        log.info("GET /api/v1/test-orders/created-by/{} - Fetching test orders", createdBy);

        List<TestOrderResponseDTO> response = testOrderService.getTestOrdersByCreatedBy(createdBy);

        return ResponseEntity.ok(response);
    }
}