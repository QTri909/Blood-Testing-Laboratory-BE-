package sum25.group03.payment_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.entities.PaymentTransaction;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;
import sum25.group03.payment_service.repositories.PaymentRequestRepository;
import sum25.group03.payment_service.repositories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

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
            Map<String, Object> captureResponse = null;

            String orderStatus = payPalService.getOrderStatus(paypalOrderId);
            if ("COMPLETED".equalsIgnoreCase(orderStatus)) {
                log.info("Order {} already captured → skip PayPal capture", paypalOrderId);
            } else {
                String responseJson = payPalService.capturePayment(paypalOrderId);
                captureResponse = captureJsonResponseToMap(responseJson);
                log.info("Captured PayPal payment for orderCode={}, paypalOrderId={}, response={}", orderCode, paypalOrderId, captureResponse);
            }

            request.setStatus(PaymentRequestStatus.SUCCESS);
            paymentRequestRepository.save(request);

            // Write PaymentTransaction if capture success => Completed => write transaction
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(paypalOrderId)
                    .status(PaymentTransactionStatus.COMPLETED)
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

    private Map<String, Object> captureJsonResponseToMap(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            return new Gson().fromJson(responseJson, type);
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", responseJson, e);
            return Map.of("error", "Failed to parse JSON response");
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

            Map<String, Object> failedResponse = Map.of("error", reason);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(oderCode)
                    .status(PaymentTransactionStatus.FAILED)
                    .rawResponse(failedResponse)
                    .build();

            paymentTransactionRepository.save(transaction);
        } else {
            log.warn("PaymentRequest not found for order {}: {}", oderCode, reason);
        }
    }
}

