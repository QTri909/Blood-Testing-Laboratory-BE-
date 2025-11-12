package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> createPayment(@Validated @RequestBody PaymentRequestRequest request) {
        try {
            PaymentRequestResponse paymentRequest = paymentRequestService.createPaymentRequest(request);

            request.setOrderCode(paymentRequest.getOrderCode());
            paymentCacheService.cachePaymentRequest(paymentRequest.getOrderCode(), request);

            String approvalUrl = payPalService.createPayment(request);

            String paypalToken = approvalUrl.split("token=")[1].split("&")[0];
            paymentCacheService.cacheTokenOrderCode(paypalToken, paymentRequest.getOrderCode());
            paymentCacheService.cacheTokenRequestId(paypalToken, paymentRequest.getId());
            String approveUrl = approvalUrl + "&orderCode=" + paymentRequest.getOrderCode();

            log.info("PaymentRequest created: orderCode={}, approveUrl={}", paymentRequest.getOrderCode(), approveUrl);
            return ApiResponse.add("Payment created", approveUrl);
        } catch (Exception e) {
            log.error("Error creating payment for orderCode={}", request.getOrderCode(), e);
            return ApiResponse.add("Failed to create payment: " + e.getMessage(), null);
        }
    }

 // process payment after payment capture(for cancel or failure (not success))
    @PostMapping("/capture")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> capturePayment(@RequestParam String token) {
        try {
            String orderCode = paymentCacheService.getOrderCodeByToken(token);
            if (orderCode == null) {
                throw new RuntimeException("OrderCode not found for token: " + token);
            }

            log.info("Mapped token {} -> orderCode {}", token, orderCode);

            paymentTransactionService.captureAndUpdateStatus(orderCode);
//            paymentTransactionService.captureAndUpdateStatus(token);

//            paymentCacheService.removeCachedPaymentRequest(orderCode);
            paymentCacheService.removeTokenOrderCode(token);

            return ApiResponse.add("Payment captured successfully for " + orderCode,
                    "Payment captured successfully for " + orderCode);
        } catch (Exception e) {
            log.error("Error capturing payment for token={}", token, e);
            return ApiResponse.add("Failed to capture payment: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{orderCode}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentRequestResponse> getPaymentStatus(@PathVariable String orderCode) {
        PaymentRequestResponse response = paymentRequestService.getByOrderCode(orderCode);
        if (response == null) {
            return ApiResponse.notFound("Payment request not found for orderCode: " + orderCode, null);
        }
        return ApiResponse.add("Payment request retrieved", response);
    }


    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<PaymentRequestResponse>> getAllByUser(@PathVariable Long userId) {
        List<PaymentRequestResponse> payments = paymentRequestService.getAllByUserId(userId);
        return ApiResponse.add("Payment requests retrieved", payments);
    }

    @PatchMapping("/{orderCode}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> updateStatus(
            @PathVariable String orderCode,
            @RequestParam String status) {
        try {
            paymentRequestService.updateStatus(orderCode, status);

            // delete cache if status is COMPLETED or FAILED
            if ("COMPLETED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
                paymentCacheService.removeCachedPaymentRequest(orderCode);
            }
            return ApiResponse.ok("Status updated successfully");

        } catch (Exception e) {
            return ApiResponse.badRequest("Failed to update status: " + e.getMessage(), null);
        }
    }

     //delete payment pending
    @DeleteMapping("/{orderCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deletePendingPayment(@PathVariable String orderCode) {
        paymentRequestService.deletePendingPayment(orderCode);
        paymentCacheService.removeCachedPaymentRequest(orderCode);
        return ApiResponse.add("Pending payment deleted successfully", null);
    }

    @GetMapping("/cache/{orderCode}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentRequestRequest> getCachedPayment(@PathVariable String orderCode) {
        PaymentRequestRequest cached = paymentCacheService.getCachedPaymentRequest(orderCode);
        if (cached == null) {
            return ApiResponse.notFound("No cached payment found for orderCode: " + orderCode, null);
        }
        return ApiResponse.add("Cached payment retrieved", cached);
    }
}