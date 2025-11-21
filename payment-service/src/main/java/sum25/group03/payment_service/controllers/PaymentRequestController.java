package sum25.group03.payment_service.controllers;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.dtos.request.PaymentEmailHelperDTO;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.helpers.PaymentEmailHelpers;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentRequestService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;
    private final PaymentTransactionService paymentTransactionService;
    private final PayPalService payPalService;
    private final PaymentCacheService paymentCacheService;
    private final PaymentEmailHelpers paymentEmailHelpers;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> createPayment(@Validated @RequestBody PaymentRequestRequest request) throws MessagingException {
        PaymentRequestResponse paymentRequest = paymentRequestService.createPaymentRequest(request);

        request.setOrderCode(paymentRequest.getOrderCode());
        paymentCacheService.cachePaymentRequest(paymentRequest.getOrderCode(), request);

        String approvalUrl = payPalService.createPayment(request);

        String paypalToken = approvalUrl.split("token=")[1].split("&")[0];
        paymentCacheService.cacheTokenOrderCode(paypalToken, paymentRequest.getOrderCode());
        paymentCacheService.cacheTokenRequestId(paypalToken, paymentRequest.getId());
        String approveUrl = approvalUrl + "&orderCode=" + paymentRequest.getOrderCode();

        // send email notification to user:
        String patientEmail = request.getPatientEmail();
        String patientName = request.getPatientName();
        String orderCode = request.getOrderCode();

        String subject = "Paypal Payment Created for Order " + orderCode;
        String additionalInfo = "Please click the link below to complete your payment for order code: " + orderCode + " with Paypal.";
        PaymentEmailHelperDTO paymentEmailHelperDTO = PaymentEmailHelperDTO.builder()
                .receiverEmail(patientEmail)
                .receiverName(patientName)
                .paymentUrl(approvalUrl)
                .additionalInfo(additionalInfo)
                .orderCode(orderCode)
                .build();

        paymentEmailHelpers.sendPaymentHtmlEmail(subject, paymentEmailHelperDTO);

        return ApiResponse.add("Payment created", approveUrl);
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

    // get all payments (for admin/manager)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<PaymentRequestResponse>> getAllPaymentRequests(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        Page<PaymentRequestResponse> paymentRequests = paymentRequestService.getAllPaymentRequests(page, size, viewerId);
        return ApiResponse.add("All payment requests retrieved", paymentRequests);
    }
}