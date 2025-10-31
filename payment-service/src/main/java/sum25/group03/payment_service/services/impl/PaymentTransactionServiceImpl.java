package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.entities.PaymentTransaction;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.TransactionStatus;
import sum25.group03.payment_service.repositories.PaymentRequestRepository;
import sum25.group03.payment_service.repositories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PayPalService payPalService;
    private final PaymentCacheServiceImpl paymentCacheService;

    @Override
    @Transactional
    public void captureAndUpdateStatus(String token) {
        try {
            // get orderCode from token(token= order_id from PayPal)
            String orderCode = paymentCacheService.getOrderCodeByToken(token);
            if (orderCode == null) {
                throw new RuntimeException("OrderCode not found for token: " + token);
            }

            // PaymentRequest only have orderCode => must be change token->oderCode
            PaymentRequest request = paymentRequestRepository
                    .findByOrderCode(orderCode)
                    .orElseThrow(() -> new RuntimeException("PaymentRequest not found for orderCode: " + orderCode));

            // token= order_id => capture payment if not status COMPLETED
            String paypalOrderId = token;
            String captureResponse = null;

            String orderStatus = payPalService.getOrderStatus(paypalOrderId);
            if ("COMPLETED".equalsIgnoreCase(orderStatus)) {
                log.info("Order {} already captured → skip PayPal capture", paypalOrderId);
            } else {
                 captureResponse = payPalService.capturePayment(paypalOrderId);
                log.info("Captured PayPal payment for orderCode={}, paypalOrderId={}, response={}", orderCode, paypalOrderId, captureResponse);
            }

            request.setStatus(PaymentRequestStatus.SUCCESS);
            paymentRequestRepository.save(request);

            // Write PaymentTransaction if capture success => Completed => write transaction
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(paypalOrderId)
                    .status(TransactionStatus.COMPLETED)
                    .rawResponse(captureResponse)
                    .build();

            paymentTransactionRepository.save(transaction);

            // delete cache
            paymentCacheService.removeCachedPaymentRequest(orderCode);
            paymentCacheService.removeTokenOrderCode(token);

        } catch (Exception e) {
            log.error("Error capturing PayPal payment: {}", token, e);
            handlePaymentFailure(token, e.getMessage());
        }
    }

    @Override
    public void handlePaymentFailure(String oderCode, String reason) {
        PaymentRequest request = paymentRequestRepository
                .findByOrderCode(oderCode)
                .orElse(null);

        if (request != null) {
            request.setStatus(PaymentRequestStatus.FAILED);
            paymentRequestRepository.save(request);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(oderCode)
                    .status(TransactionStatus.FAILED)
                    .rawResponse("{\"error\": \"" + reason + "\"}")
                    .build();

            paymentTransactionRepository.save(transaction);
        } else {
            log.warn("PaymentRequest not found for order {}: {}", oderCode, reason);
        }
    }
}

