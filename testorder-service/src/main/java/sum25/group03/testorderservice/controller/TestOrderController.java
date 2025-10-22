package sum25.group03.testorderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dto.request.TestOrderRequestDTO;
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
            @Valid @RequestBody TestOrderRequestDTO requestDTO) {
        log.info("PUT /api/v1/test-orders/{} - Updating test order", id);

        TestOrderResponseDTO response = testOrderService.updateTestOrder(id, requestDTO);

        log.info("Test order updated successfully with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestOrder(@PathVariable Long id) {
        log.info("DELETE /api/v1/test-orders/{} - Deleting test order", id);

        testOrderService.deleteTestOrder(id);

        log.info("Test order deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponseDTO> getTestOrderById(@PathVariable Long id) {
        log.info("GET /api/v1/test-orders/{} - Fetching test order", id);

        TestOrderResponseDTO response = testOrderService.getTestOrderById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TestOrderResponseDTO>> getAllTestOrders() {
        log.info("GET /api/v1/test-orders - Fetching all test orders");

        List<TestOrderResponseDTO> response = testOrderService.getAllTestOrders();

        return ResponseEntity.ok(response);
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
            @RequestParam TestOrderStatus status) {
        log.info("PATCH /api/v1/test-orders/{}/status - Updating status to {}", id, status);

        TestOrderResponseDTO response = testOrderService.updateTestOrderStatus(id, status);

        log.info("Test order status updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }
}