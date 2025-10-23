package sum25.group03.testorderservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.testorderservice.dto.request.TestOrderFiltering;
import sum25.group03.testorderservice.dto.request.TestOrderRequest;
import sum25.group03.testorderservice.dto.response.TestOrderResponse;
import sum25.group03.testorderservice.enums.ActionTypeFutures;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.services.impl.ActionLogService;
import sum25.group03.testorderservice.services.interfaces.ITestOrderService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test-orders")
@RequiredArgsConstructor
public class TestOrderController {

    private final ITestOrderService service;
    private final ActionLogService actionLogService;

    // BE-1: GET /test-orders
    @GetMapping
    public ResponseEntity<List<TestOrderResponse>> getAllTestOrders(@RequestParam Long viewerId) {
        actionLogService.logAction(viewerId, ActionTypeFutures.VIEW_TEST_ORDER_LIST, null);
        log.info("User {} requested all test orders", viewerId);
        return ResponseEntity.ok(service.getAllTestOrders());
    }

    // BE-1: GET /test-orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponse> getById(@PathVariable Long id, @RequestParam Long viewerId) {
        actionLogService.logAction(viewerId, ActionTypeFutures.VIEW_TEST_ORDER_DETAIL, id);
        log.info("User {} requested test order with id {}", viewerId, id);


        return ResponseEntity.ok(service.getTestOrderById(id));
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
        actionLogService.logAction(viewerId, ActionTypeFutures.VIEW_TEST_ORDER_LIST, null);
        log.info("User {} requested filtered test orders with criteria: {}", viewerId, filterInfo);
        return ResponseEntity.ok(service.filterTestOrders(filterInfo));
    }
}
