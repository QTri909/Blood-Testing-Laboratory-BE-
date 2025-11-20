package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.dtos.request.RequestTransactionsByRequestId;
import sum25.group03.payment_service.dtos.response.PaymentTransactionRes;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;

@RestController
@RequestMapping("/api/v1/payment-transactions")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @GetMapping("/{paymentRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<PaymentTransactionRes>> getAllTransactionsByPaymentRequestId(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @PathVariable("paymentRequestId") String paymentRequestId,
            @RequestHeader("X-User-Id") Long viewerId
    ) {
        RequestTransactionsByRequestId request = new RequestTransactionsByRequestId(page, size, paymentRequestId, viewerId);
        Page<PaymentTransactionRes> responses = paymentTransactionService.getAllTransactionsByPaymentRequestId(request);
        return ApiResponse.add("Get all transactions by payment request id successfully", responses);
    }
}
