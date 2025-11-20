package sum25.group03.payment_service.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.payment_service.dtos.request.RequestTransactionsByRequestId;
import sum25.group03.payment_service.dtos.response.PaymentTransactionRes;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.entities.PaymentTransaction;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;
import sum25.group03.payment_service.mappers.PaymentTransactionMapper;
import sum25.group03.payment_service.repositories.PaymentRequestRepository;
import sum25.group03.payment_service.repositories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.PayPalService;
import sum25.group03.payment_service.services.interfaces.PaymentTransactionService;

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
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Override
    @Transactional
    public void captureAndUpdateStatus(String token) {
        try {
            // token -> requestId
            String requestId = paymentCacheService.getRequestIdByToken(token);
            if (requestId == null) {
                throw new RuntimeException("RequestId not found for token: " + token);
            }

            // requestId -> payment request
            PaymentRequest request = paymentRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("PaymentRequest not found for id=" + requestId));

            String paypalOrderId = token;
            Map<String, Object> captureResponse = null;

            String orderStatus = payPalService.getOrderStatus(paypalOrderId);
            log.info("PayPal order {} status before capture: {}", paypalOrderId, orderStatus);

            if (!"COMPLETED".equalsIgnoreCase(orderStatus)) {
                String responseJson = payPalService.capturePayment(paypalOrderId);
                captureResponse = parseJsonToMap(responseJson);
                log.info("Captured PayPal payment for requestId={}, paypalOrderId={}", requestId, paypalOrderId);
            } else {
                log.info("Order {} already completed on PayPal → skip capture", paypalOrderId);
            }

            request.setStatus(PaymentRequestStatus.SUCCESS);
            paymentRequestRepository.save(request);
            //write transaction
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(paypalOrderId)
                    .status(PaymentTransactionStatus.COMPLETED)
                    .rawResponse(captureResponse)
                    .build();

            paymentTransactionRepository.save(transaction);

            paymentCacheService.removeCachedPaymentRequest(request.getOrderCode());
            paymentCacheService.removeTokenRequestId(token);
            paymentCacheService.removeTokenOrderCode(token);

            log.info("Payment capture completed successfully for requestId={}, token={}", requestId, token);

        } catch (Exception e) {
            log.error("Error capturing PayPal payment for token={}", token, e);
            handlePaymentFailure(token, e.getMessage());
        }
    }

    private Map<String, Object> parseJsonToMap(String responseJson) {
        try {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            return new Gson().fromJson(responseJson, type);
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", responseJson, e);
            return Map.of("error", "Failed to parse JSON response");
        }
    }

    @Override
    public void handlePaymentFailure(String token, String reason) {
        try {
            String requestId = paymentCacheService.getRequestIdByToken(token);
            if (requestId == null) {
                log.warn("Cannot find requestId for failed token={}", token);
                return;
            }

            PaymentRequest request = paymentRequestRepository.findById(requestId)
                    .orElse(null);

            if (request == null) {
                log.warn("PaymentRequest not found for requestId={}", requestId);
                return;
            }

            request.setStatus(PaymentRequestStatus.FAILED);
            paymentRequestRepository.save(request);

            Map<String, Object> failedResponse = Map.of("error", reason);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentRequest(request)
                    .gatewayTransactionId(token)
                    .status(PaymentTransactionStatus.FAILED)
                    .rawResponse(failedResponse)
                    .build();

            paymentTransactionRepository.save(transaction);

            paymentCacheService.removeCachedPaymentRequest(request.getOrderCode());
            paymentCacheService.removeTokenRequestId(token);
            paymentCacheService.removeTokenOrderCode(token);

            log.info("Marked payment as FAILED for requestId={} (token={})", requestId, token);

        } catch (Exception ex) {
            log.error("Error while handling failed payment for token={}: {}", token, ex.getMessage());
        }
    }

    @Override
    public Page<PaymentTransactionRes> getAllTransactionsByPaymentRequestId(RequestTransactionsByRequestId request) {

        int page = request.page();
        int size = request.size();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PaymentTransaction> transactionsPage = paymentTransactionRepository.findAll(pageable);
        return paymentTransactionMapper.toTransactionResDTOPage(transactionsPage);
    }
}

