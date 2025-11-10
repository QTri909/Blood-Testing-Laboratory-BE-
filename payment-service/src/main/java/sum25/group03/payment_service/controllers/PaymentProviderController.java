package sum25.group03.payment_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PaymentProviderResponse> create(@RequestBody PaymentProviderRequest request) {
        return ApiResponse.add("Payment provider created successfully", paymentProviderService.create(request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentProviderResponse> update(@PathVariable String id, @RequestBody PaymentProviderRequest request) {
        return ApiResponse.add("Payment provider updated successfully", paymentProviderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable String id) {
        paymentProviderService.delete(id);
        return ApiResponse.add("Payment provider deleted successfully", null);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentProviderResponse> getById(@PathVariable String id) {
        return ApiResponse.add("Payment provider retrieved successfully", paymentProviderService.getById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<PaymentProviderResponse>> getAll() {
        return ApiResponse.add("Payment providers retrieved successfully", paymentProviderService.getAll());
    }
}