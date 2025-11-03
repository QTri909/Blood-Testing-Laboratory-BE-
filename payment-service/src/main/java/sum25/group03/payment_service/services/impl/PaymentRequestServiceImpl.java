package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sum25.group03.payment_service.dtos.request.PaymentRequestRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.mappers.PaymentRequestMapper;
import sum25.group03.payment_service.repositories.PaymentProviderRepository;
import sum25.group03.payment_service.repositories.PaymentRequestRepository;
import sum25.group03.payment_service.repositories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.PaymentRequestService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestServiceImpl implements PaymentRequestService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentProviderRepository paymentProviderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRequestMapper paymentRequestMapper;

    @Override
    public PaymentRequestResponse createPaymentRequest(PaymentRequestRequest request) {
        PaymentRequest entity = paymentRequestMapper.toEntity(request);
        PaymentProvider provider = paymentProviderRepository.findById(request.getPaymentProviderId())
                .orElseThrow(() -> new IllegalArgumentException("Payment provider not found"));
        entity.setPaymentProvider(provider);
        entity.setStatus(PaymentRequestStatus.PENDING);
        PaymentRequest saved = paymentRequestRepository.save(entity);
        return paymentRequestMapper.toResponse(saved);

    }

    @Override
    public PaymentRequestResponse getByOrderCode(String orderCode) {
        PaymentRequest entity = paymentRequestRepository.findByOrderCode(orderCode)
                .orElse(null);
        return entity != null ? paymentRequestMapper.toResponse(entity) : null;
    }

    @Override
    public List<PaymentRequestResponse> getAllByUserId(Long userId) {
        List<PaymentRequest> entities = paymentRequestRepository.findAllByUserId(userId);
        return entities.stream()
                .map(paymentRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String orderCode, String status) {
        PaymentRequest entity = paymentRequestRepository.findByOrderCode(orderCode)
                .orElse(null);
        if (entity != null) {
            entity.setStatus(PaymentRequestStatus.valueOf(status));
            paymentRequestRepository.save(entity);
        }
    }

    @Override
    public void deletePendingPayment(String orderCode) {
        PaymentRequest entity = paymentRequestRepository.findByOrderCode(orderCode)
                .orElse(null);
        if (entity != null && entity.getStatus() == PaymentRequestStatus.PENDING) {
            paymentRequestRepository.delete(entity);
        }
    }
}