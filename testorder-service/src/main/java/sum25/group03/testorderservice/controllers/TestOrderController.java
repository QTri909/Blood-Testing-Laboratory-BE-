package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.enums.ActionTypeFeatures;
import sum25.group03.testorderservice.services.interfaces.ITestOrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test-orders")
@RequiredArgsConstructor
public class TestOrderController {

    private final ITestOrderService service;

    // BE-1: GET /test-orders
    @GetMapping
    public ResponseEntity<List<TestOrderResponse>> getAllTestOrders(@RequestParam Long viewerId) {
        return ResponseEntity.ok(service.getAllTestOrders(viewerId));
    }

    // BE-1: GET /test-orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponse> getById(@PathVariable Long id, @RequestParam Long viewerId) {
        return ResponseEntity.ok(service.getTestOrderById(id, viewerId));
    }

    // BE-3: POST /test-orders
    @PostMapping
    public ResponseEntity<TestOrderResponse> create(@RequestBody TestOrderRequest dto) {
        return ResponseEntity.ok(service.createTestOrder(dto));
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
            @ModelAttribute TestOrderFiltering filterInfo,
            @RequestParam Long viewerId
    ) {
        return ResponseEntity.ok(service.filterTestOrders(filterInfo, viewerId));
    }
}
