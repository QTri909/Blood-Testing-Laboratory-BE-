package sum25.group03.payment_service.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import sum25.group03.common.response.ApiResponse;
import sum25.group03.payment_service.dtos.request.VNPayCreatePaymentRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.dtos.response.PaymentResponseDTO;
import sum25.group03.payment_service.dtos.response.VNPayCreatePaymentResponse;
import sum25.group03.payment_service.helpers.EmailHelpers;
import sum25.group03.payment_service.services.interfaces.EmailService;
import sum25.group03.payment_service.services.interfaces.VNPayService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments/vnpay")
public class VNPayController {
    private final VNPayService service;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontEndUrl;

    public VNPayController(VNPayService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    /*
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<VNPayCreatePaymentResponse> create(@Valid @RequestBody VNPayCreatePaymentRequest req, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        return ApiResponse.add("VNPay payment created", service.create(req, ip));
    }
    */

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> create(
            @Valid @RequestBody VNPayCreatePaymentRequest req,
            HttpServletRequest http
    ) throws MessagingException {
        String ip = http.getRemoteAddr();

        VNPayCreatePaymentResponse response = service.create(req, ip);

        // send email notification to user:
        String patientEmail = req.getPatientEmail();
        String patientName = req.getPatientName();
        String orderCode = req.getOrderCode();
        String paymentUrl = response.getPaymentUrl();

        String subject = "VNPay Payment Created for Order " + orderCode;
        String body = EmailHelpers.getVnPayPaymentEmailBody(patientName, paymentUrl);

        emailService.sendHtmlEmail(patientEmail, subject, body);
        // return ApiResponse.add("VNPay payment created", response);
        return ApiResponse.add("[LAB] VNPay payment created and email sent", paymentUrl);
    }

    @RequestMapping(path = "/ipn", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Map<String, String>> ipn(@RequestParam MultiValueMap<String, String> params) {

        Map<String, String> flat = new HashMap<>();
        // use getFirst() to avoid list indexing warning and handle single-valued params
        params.forEach((k, v) -> flat.put(k, params.getFirst(k)));
        return ApiResponse.add("IPN handled", service.handleIpn(flat));
    }

    @GetMapping(path = "/return")
    public RedirectView returnUrl(@RequestParam Map<String, String> params) {
        // handle return
        PaymentResponseDTO response = service.handleReturn(params);

        // Build redirect URL with query parameters
        String urlRedirect = String.format("%s/payment/result?status=%s&order_code=%s&transaction_id=%s",
                frontEndUrl,
                response.getStatus(),
                response.getOrderCode(),
                response.getTxnRef());

        return new RedirectView(urlRedirect);
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
