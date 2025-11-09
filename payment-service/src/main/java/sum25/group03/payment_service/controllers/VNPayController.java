package sum25.group03.payment_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.dtos.request.VNPayCreatePaymentRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.dtos.response.PaymentResponseDTO;
import sum25.group03.payment_service.dtos.response.VNPayCreatePaymentResponse;
import sum25.group03.payment_service.services.interfaces.VNPayService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments/vnpay")
public class VNPayController {
    private final VNPayService service;

    public VNPayController(VNPayService service) {
        this.service = service;
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VNPayCreatePaymentResponse> create(@Valid @RequestBody VNPayCreatePaymentRequest req, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        return ApiResponse.add("VNPay payment created", service.create(req, ip));
    }

    @RequestMapping(path = "/ipn", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Map<String, String>> ipn(@RequestParam MultiValueMap<String, String> params) {

        log.info("ipn::VNPay has called this!");
        log.info("ipn::VNPay give this to me: {}", params.toString());

        Map<String, String> flat = new HashMap<>();
        // use getFirst() to avoid list indexing warning and handle single-valued params
        params.forEach((k, v) -> flat.put(k, params.getFirst(k)));
        return ApiResponse.add("IPN handled", service.handleIpn(flat));
    }

    @GetMapping(path = "/return")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentResponseDTO> returnUrl(@RequestParam Map<String, String> params) {

        log.info("return::VNPay return called this!");
        log.info("return::VNPay give this to me: {}", params.toString());

        return ApiResponse.add("VNPay return handled", service.handleReturn(params));
    }

    @Transactional
    @GetMapping(path="/requests/{txnRef}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentRequestResponse> getPaymentRequestByTxnRef(@PathVariable String txnRef) {
        return ApiResponse.add("Get payment request by txnRef", service.queryRequestByTxnRef(txnRef));
    }

    @GetMapping(path = "/poll/{txnRef}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentResponseDTO> poll(@PathVariable String txnRef) {
        return ApiResponse.add("VNPay poll result", service.queryByTxnRef(txnRef));
    }

    @GetMapping(path = "/query/{txnRef}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PaymentResponseDTO> query(@PathVariable String txnRef) {
        return ApiResponse.add("VNPay query result", service.queryByTxnRef(txnRef));
    }
}
