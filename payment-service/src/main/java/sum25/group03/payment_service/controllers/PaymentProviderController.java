package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.payment_service.dtos.request.PaymentProviderRequest;
import sum25.group03.payment_service.dtos.response.PaymentProviderResponse;
import sum25.group03.payment_service.services.interfaces.PaymentProviderService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-providers")
@RequiredArgsConstructor
public class PaymentProviderController {

    private final PaymentProviderService paymentProviderService;

    @PostMapping
    public ResponseEntity<PaymentProviderResponse> create(@RequestBody PaymentProviderRequest request) {
        return ResponseEntity.ok(paymentProviderService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentProviderResponse> update(@PathVariable String id, @RequestBody PaymentProviderRequest request) {
        return ResponseEntity.ok(paymentProviderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        paymentProviderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentProviderResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(paymentProviderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentProviderResponse>> getAll() {
        return ResponseEntity.ok(paymentProviderService.getAll());
    }
}