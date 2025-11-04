package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.payment_service.dtos.request.PaymentProviderRequest;
import sum25.group03.payment_service.dtos.response.PaymentProviderResponse;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.mappers.PaymentProviderMapper;
import sum25.group03.payment_service.repositories.PaymentProviderRepository;
import sum25.group03.payment_service.services.interfaces.PaymentProviderService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentProviderServiceImpl implements PaymentProviderService {

    private final PaymentProviderRepository paymentProviderRepository;
    private final PaymentProviderMapper paymentProviderMapper;

    @Override
    @Transactional
    public PaymentProviderResponse create(PaymentProviderRequest request) {
        PaymentProvider entity = paymentProviderMapper.toEntity(request);
        entity = paymentProviderRepository.save(entity);
        return paymentProviderMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public PaymentProviderResponse update(String id, PaymentProviderRequest request) {
        PaymentProvider entity = paymentProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        paymentProviderMapper.updateEntity(request, entity);
        entity = paymentProviderRepository.save(entity);
        return paymentProviderMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        paymentProviderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentProviderResponse getById(String id) {
        PaymentProvider entity = paymentProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        return paymentProviderMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentProviderResponse> getAll() {
        return paymentProviderRepository.findAll()
                .stream()
                .map(paymentProviderMapper::toResponse)
                .collect(Collectors.toList());
    }
}