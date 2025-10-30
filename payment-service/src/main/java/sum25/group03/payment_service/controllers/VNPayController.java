package sum25.group03.payment_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import sum25.group03.payment_service.dtos.*;
import sum25.group03.payment_service.services.interfaces.VNPayService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment/vnpay")
public class VNPayController {
    private final VNPayService service;

    public VNPayController(VNPayService service) {
        this.service = service;
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public VNPayCreatePaymentResponse create(@Valid @RequestBody VNPayCreatePaymentRequest req, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        return service.create(req, ip);
    }

    @PostMapping(path = "/ipn")
    public Map<String, String> ipn(@RequestParam MultiValueMap<String, String> params) {
        Map<String, String> flat = new HashMap<>();
        params.forEach((k, v) -> flat.put(k, v.get(0)));
        return service.handleIpn(flat);
    }

    @GetMapping(path = "/return")
    public PaymentResponseDTO returnUrl(@RequestParam Map<String, String> params) {
        return service.handleReturn(params);
    }

    @GetMapping(path = "/poll/{txnRef}")
    public PaymentResponseDTO poll(@PathVariable String txnRef) {
        return service.queryByTxnRef(txnRef);
    }

    @GetMapping(path = "/query/{txnRef}")
    public PaymentResponseDTO query(@PathVariable String txnRef) {
        return service.queryByTxnRef(txnRef);
    }
}
