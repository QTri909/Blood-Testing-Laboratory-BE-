package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.services.interfaces.ITestOrderService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/test-orders")
@RequiredArgsConstructor
public class TestOrderController {

    private final ITestOrderService service;

    // BE-1: GET /test-orders
    @GetMapping
    public ResponseEntity<List<TestOrderResponse>> getAllTestOrders() {
        return ResponseEntity.ok(service.getAllTestOrders());
    }

    // BE-1: GET /test-orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTestOrderById(id));
    }

    // BE-3: POST /test-orders
    @PostMapping
    public ResponseEntity<TestOrderResponse> create(@RequestBody TestOrderRequest dto) {
        return ResponseEntity.ok(service.createTestOrder(dto));
    }

    // BE-4: PUT /test-orders/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TestOrderResponse> update(
            @PathVariable Long id,
            @RequestBody TestOrderRequest dto) {
        return ResponseEntity.ok(service.updateTestOrder(id, dto));
    }

    // BE-5: DELETE /test-orders/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTestOrder(id);
        return ResponseEntity.noContent().build();
    }

    // BE-6: GET /test-orders/filter
    @GetMapping("/filter")
    public ResponseEntity<List<TestOrderResponse>> filterTestOrders(
            @ModelAttribute TestOrderFiltering filterInfo
    ) {
        return ResponseEntity.ok(service.filterTestOrders(filterInfo));
    }
}
