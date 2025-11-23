package sum25.group03.payment_service.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import sum25.group03.payment_service.configs.VNPayProperties;
import sum25.group03.payment_service.dtos.request.VNPayCreatePaymentRequest;
import sum25.group03.payment_service.dtos.response.PaymentRequestResponse;
import sum25.group03.payment_service.dtos.response.PaymentResponseDTO;
import sum25.group03.payment_service.dtos.response.VNPayCreatePaymentResponse;
import sum25.group03.payment_service.entities.PaymentProvider;
import sum25.group03.payment_service.entities.PaymentRequest;
import sum25.group03.payment_service.entities.PaymentTransaction;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;
import sum25.group03.payment_service.helpers.VNPayHelpers;
import sum25.group03.payment_service.mappers.PaymentRequestMapper;
import sum25.group03.payment_service.mappers.PaymentStatusMapper;
import sum25.group03.payment_service.repositories.PaymentProviderRepository;
import sum25.group03.payment_service.repositories.PaymentRequestRepository;
import sum25.group03.payment_service.repositories.PaymentTransactionRepository;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;
import sum25.group03.payment_service.services.interfaces.QRCodeService;
import sum25.group03.payment_service.services.interfaces.VNPayService;
import sum25.group03.payment_service.utils.MapUtils;
import sum25.group03.payment_service.utils.UUIDUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayServiceImpl implements VNPayService {
    private final VNPayProperties props;
    private final PaymentCacheService cache;
    private final QRCodeService qr;
    private final PaymentRequestMapper paymentRequestMapper;

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentProviderRepository paymentProviderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    private final String TXN_REF_PREFIX = "TR_";

    @Override
    @Transactional
    public VNPayCreatePaymentResponse create(VNPayCreatePaymentRequest req, String clientIp) {

        if (!UUIDUtils.isValidUUID(req.getOrderCode()))
            throw new IllegalArgumentException("Invalid orderCode format, must be an UUID");

        // Validate required configuration early to avoid NPE during signing when values are missing
        requireNonBlank(props.getTmnCode(), "VNPay configuration missing: tmnCode (env VNP_TMN_CODE or property vnpay.tmn-code)");
        requireNonBlank(props.getHashSecret(), "VNPay configuration missing: hashSecret (env VNP_HASH_SECRET or property vnpay.hash-secret)");
        requireNonBlank(props.getPaymentUrl(), "VNPay configuration missing: paymentUrl (env VNP_URL or property vnpay.payment-url)");
        requireNonBlank(props.getReturnUrl(), "VNPay configuration missing: returnUrl (env VNP_RETURN_URL or property vnpay.return-url)");

        String txnRef = TXN_REF_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", props.getVersion());
        params.put("vnp_Command", props.getCommand());
        params.put("vnp_TmnCode", props.getTmnCode());
        params.put("vnp_Amount", String.valueOf(req.getAmount() * 100));
        params.put("vnp_CurrCode", req.getCurrency().toString());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", Optional.ofNullable(req.getOrderInfo()).orElse("Payment " + req.getOrderCode()));
//        params.put("vnp_OrderInfo", "Payment for order " + req.getOrderCode());
        params.put("vnp_OrderType", props.getOrderType());
        params.put("vnp_Locale", Optional.ofNullable(req.getLocale().toString()).orElse("vn"));
        params.put("vnp_ReturnUrl", props.getReturnUrl());
        String safeClientIp = (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp;
        params.put("vnp_IpAddr", safeClientIp);
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        if (req.getBankCode() != null && !req.getBankCode().isBlank()) params.put("vnp_BankCode", req.getBankCode());

        String data = VNPaySigner.canonicalQuery(params);
        String secureHash = VNPaySigner.hmacSHA512(props.getHashSecret(), data);
//        String paymentUrl = UriComponentsBuilder.fromHttpUrl(props.getPaymentUrl())
//                .query(data + "&vnp_SecureHash=" + secureHash)
//                //.build(true)
//                .toUriString();

        // Manually build the URL — do not let Spring re-encode
        String paymentUrl = props.getPaymentUrl() + "?" + data + "&vnp_SecureHash=" + secureHash;

        // persist PaymentRequest entity
        PaymentProvider vnPayProviderInfo = paymentProviderRepository.findByCodeAndStatus(PaymentProviderCode.VN_PAY, PaymentProviderStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("VNPay PaymentProvider is not active for current"));

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderCode(req.getOrderCode())
                .userId(req.getUserId())
                .amount(req.getAmount().doubleValue())
                .currency(req.getCurrency())
                .status(PaymentRequestStatus.PENDING) // default
                .txnRef(txnRef)
                .paymentProvider(vnPayProviderInfo)
                .build();

        // checking any existing payment request with same orderCode and PENDING status
        List<PaymentRequest> existingRequestsOpt = paymentRequestRepository.findAllByOrderCodeAndStatus(req.getOrderCode(), PaymentRequestStatus.PENDING)
                .orElse(List.of());
        for (PaymentRequest existingRequest: existingRequestsOpt) {
            existingRequest.setStatus(PaymentRequestStatus.CANCELLED);
        }


        paymentRequestRepository.save(paymentRequest);

        // generate response
        VNPayCreatePaymentResponse resp = new VNPayCreatePaymentResponse();
        resp.setPaymentUrl(paymentUrl);
        resp.setTxnRef(txnRef);
        resp.setAmount(req.getAmount());

        // process in case we need to generate QR code
        if (req.isGenerateQRCode()) {
            int w = Optional.ofNullable(req.getQrWidth()).orElse(props.getQrCode().getDefaultWidth());
            int h = Optional.ofNullable(req.getQrHeight()).orElse(props.getQrCode().getDefaultHeight());
            var qrOut = qr.generate(paymentUrl, w, h);
            resp.setQrCodeBase64(qrOut.base64());
            resp.setQrCodeDataUrl(qrOut.dataUrl());
        }

        // Cache the PENDING status with a TTL of 15 minutes
        cache.putStatus(txnRef, "PENDING", Duration.ofMinutes(15));
        resp.setCachedInRedis(true);
        resp.setCacheTTL(15L * 60);
        return resp;
    }

    private static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(message);
        }
    }

    @Override
    @Transactional
    public Map<String, String> handleIpn(Map<String, String> params) {
        String provided = params.get("vnp_SecureHash");
        Map<String, String> toSign = new HashMap<>(params);
        toSign.remove("vnp_SecureHash");
        String data = VNPaySigner.canonicalQuery(toSign);
        String expected = VNPaySigner.hmacSHA512(props.getHashSecret(), data);
        if (!expected.equalsIgnoreCase(provided)) {
            return Map.of("RspCode", "97", "Message", "Invalid signature");
        }

        String txnRef = params.get("vnp_TxnRef");
        String gatewayTransactionNo = params.get("vnp_TransactionNo");
        String rsp = params.getOrDefault("vnp_ResponseCode", "99");
        PaymentTransactionStatus mappedStatus = VNPayHelpers.mapVnPayResponseFromResponseCode(rsp);

        // Fast path: cache then async DB persist
        String mappedStatusStr = mappedStatus.toString();
        cache.putStatus(txnRef, mappedStatusStr,
                mappedStatusStr.equals("SUCCESS") ? Duration.ofHours(24) : Duration.ofHours(1)
        );

        // persist PaymentTransaction
        PaymentRequest searchedPaymentRequest = paymentRequestRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new IllegalStateException("No PaymentRequest found for transaction: " + txnRef));

        Map<String, Object> updates = MapUtils.toObjectMap(params);
        PaymentTransaction newTransaction = PaymentTransaction.builder()
                .paymentRequest(searchedPaymentRequest)
                .gatewayTransactionId(gatewayTransactionNo)
                .status(mappedStatus)
                .gatewayStatusCode(rsp)
                .rawResponse(updates)
                .build();
        paymentTransactionRepository.save(newTransaction);

        // update PaymentRequest:
        PaymentRequestStatus mappedPaymentStatus = PaymentStatusMapper.toRequestStatus(mappedStatus);
        searchedPaymentRequest.setStatus(mappedPaymentStatus);

        return Map.of("RspCode", rsp, "Status", mappedStatusStr);
    }

    @Override
    public PaymentResponseDTO handleReturn(Map<String, String> params) {
        String provided = params.get("vnp_SecureHash");
        Map<String, String> toSign = new HashMap<>(params);
        toSign.remove("vnp_SecureHash");
        String data = VNPaySigner.canonicalQuery(toSign);
        String expected = VNPaySigner.hmacSHA512(props.getHashSecret(), data);
        if (!expected.equalsIgnoreCase(provided)) {
            var dto = new PaymentResponseDTO();
            dto.setStatus("FAILED");
            dto.setMessage("Invalid signature");
            dto.setDataSource("VALIDATION");
            return dto;
        }
        String txnRef = params.get("vnp_TxnRef");
        var dto = new PaymentResponseDTO();
        dto.setTxnRef(txnRef);
        dto.setOrderCode(params.get("vnp_OrderInfo"));
        String status = cache.getStatus(txnRef).orElse("UNKNOWN");
        dto.setStatus(status);
        dto.setDataSource(status.equals("UNKNOWN") ? "DATABASE" : "REDIS");
        return dto;
    }

    @Override
    public PaymentResponseDTO queryByTxnRef(String txnRef) {
        var dto = new PaymentResponseDTO();
        dto.setTxnRef(txnRef);
        String status = cache.getStatus(txnRef).orElse("UNKNOWN");
        dto.setStatus(status);
        dto.setDataSource(status.equals("UNKNOWN") ? "VNPAY_API" : "REDIS");
        return dto;
    }

    @Override
    public PaymentRequestResponse queryRequestByTxnRef(String txnRef) {
        PaymentRequest entity = paymentRequestRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new IllegalStateException("No PaymentRequest found for transaction: " + txnRef));
        return paymentRequestMapper.toResponse(entity);
    }
}
