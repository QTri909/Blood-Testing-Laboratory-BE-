package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentRequestService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;
    private final PaymentTransactionService paymentTransactionService;
    private final PayPalService payPalService;
    private final PaymentCacheService paymentCacheService;

    @PostMapping
    public ResponseEntity<String> createPayment(@Validated @RequestBody PaymentRequestRequest request) {
        try {
            PaymentRequestResponse paymentRequest = paymentRequestService.createPaymentRequest(request);

            request.setOrderCode(paymentRequest.getOrderCode());
            paymentCacheService.cachePaymentRequest(paymentRequest.getOrderCode(), request);

            String approvalUrl = payPalService.createPayment(request);

            String paypalToken = approvalUrl.split("token=")[1].split("&")[0];
            paymentCacheService.cacheTokenOrderCode(paypalToken, paymentRequest.getOrderCode());
            String approveUrl = approvalUrl + "&orderCode=" + paymentRequest.getOrderCode();

            log.info("PaymentRequest created: orderCode={}, approveUrl={}", paymentRequest.getOrderCode(), approveUrl);
            return ResponseEntity.status(201).body(approveUrl);
        } catch (Exception e) {
            log.error("Error creating payment for orderCode={}", request.getOrderCode(), e);
            return ResponseEntity.badRequest().body("Failed to create payment: " + e.getMessage());
        }
    }

 // process payment after payment capture(for cancel or failure (not success))
    @PostMapping("/capture")
    public ResponseEntity<String> capturePayment(@RequestParam String token) {
        try {
            String orderCode = paymentCacheService.getOrderCodeByToken(token);
            if (orderCode == null) {
                throw new RuntimeException("OrderCode not found for token: " + token);
            }

            log.info("Mapped token {} -> orderCode {}", token, orderCode);

            paymentTransactionService.captureAndUpdateStatus(orderCode);

            paymentCacheService.removeCachedPaymentRequest(orderCode);
            paymentCacheService.removeTokenOrderCode(token);

            return ResponseEntity.ok("Payment captured successfully for " + orderCode);
        } catch (Exception e) {
            log.error("Error capturing payment for token={}", token, e);
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<PaymentRequestResponse> getPaymentStatus(@PathVariable String orderCode) {
        PaymentRequestResponse response = paymentRequestService.getByOrderCode(orderCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentRequestResponse>> getAllByUser(@PathVariable Long userId) {
        List<PaymentRequestResponse> payments = paymentRequestService.getAllByUserId(userId);
        log.info("Retrieved {} payment requests for userId={}", payments.size(), userId);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{orderCode}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable String orderCode,
            @RequestParam String status) {
        try {
            paymentRequestService.updateStatus(orderCode, status);
            log.info("Updated payment status for orderCode={} to {}", orderCode, status);

            // delete cache if status is COMPLETED or FAILED
            if ("COMPLETED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
                paymentCacheService.removeCachedPaymentRequest(orderCode);
            }

            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            log.error("Error updating payment status for orderCode={}", orderCode, e);
            return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
        }
    }

     //delete payment pending
    @DeleteMapping("/{orderCode}")
    public ResponseEntity<String> deletePendingPayment(@PathVariable String orderCode) {
        paymentRequestService.deletePendingPayment(orderCode);
        paymentCacheService.removeCachedPaymentRequest(orderCode);

        log.info("Deleted pending payment for orderCode={}", orderCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cache/{orderCode}")
    public ResponseEntity<PaymentRequestRequest> getCachedPayment(@PathVariable String orderCode) {
        PaymentRequestRequest cached = paymentCacheService.getCachedPaymentRequest(orderCode);
        if (cached == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cached);
    }
}