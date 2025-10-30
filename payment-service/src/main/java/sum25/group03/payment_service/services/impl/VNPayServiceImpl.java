package sum25.group03.payment_service.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import sum25.group03.payment_service.configs.VNPayProperties;
import sum25.group03.payment_service.dtos.*;
import sum25.group03.payment_service.services.interfaces.PaymentCacheService;
import sum25.group03.payment_service.services.interfaces.QRCodeService;
import sum25.group03.payment_service.services.interfaces.VNPayService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {
    private final VNPayProperties props;
    private final PaymentCacheService cache;
    private final QRCodeService qr;

    public VNPayServiceImpl(VNPayProperties props, PaymentCacheService cache, QRCodeService qr) {
        this.props = props; this.cache = cache; this.qr = qr;
    }

    @Override
    public VNPayCreatePaymentResponse create(VNPayCreatePaymentRequest req, String clientIp) {
        String txnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 18);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", props.getVersion());
        params.put("vnp_Command", props.getCommand());
        params.put("vnp_TmnCode", props.getTmnCode());
        params.put("vnp_Amount", String.valueOf(req.getAmount() * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", Optional.ofNullable(req.getOrderInfo()).orElse("Payment " + req.getOrderCode()));
        params.put("vnp_OrderType", props.getOrderType());
        params.put("vnp_Locale", Optional.ofNullable(req.getLocale()).orElse(props.getLocale()));
        params.put("vnp_ReturnUrl", props.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        if (req.getBankCode() != null && !req.getBankCode().isBlank()) params.put("vnp_BankCode", req.getBankCode());

        String data = VNPaySigner.canonicalQuery(params);
        String secureHash = VNPaySigner.hmacSHA512(props.getHashSecret(), data);
        String paymentUrl = UriComponentsBuilder.fromHttpUrl(props.getPaymentUrl())
                .query(data + "&vnp_SecureHash=" + secureHash)
                .build(true)
                .toUriString();

        // TODO: persist PaymentRequest with PENDING status and txnRef

        VNPayCreatePaymentResponse resp = new VNPayCreatePaymentResponse();
        resp.setPaymentUrl(paymentUrl);
        resp.setTxnRef(txnRef);
        resp.setAmount(req.getAmount());

        if (req.isGenerateQRCode()) {
            int w = Optional.ofNullable(req.getQrWidth()).orElse(props.getQrCode().getDefaultWidth());
            int h = Optional.ofNullable(req.getQrHeight()).orElse(props.getQrCode().getDefaultHeight());
            var qrOut = qr.generate(paymentUrl, w, h);
            resp.setQrCodeBase64(qrOut.base64());
            resp.setQrCodeDataUrl(qrOut.dataUrl());
        }

        cache.putStatus(txnRef, "PENDING", Duration.ofMinutes(15));
        resp.setCachedInRedis(true);
        resp.setCacheTTL(15L * 60);
        return resp;
    }

    @Override
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
        String rsp = params.getOrDefault("vnp_ResponseCode", "99");
        String status = switch (rsp) {
            case "00" -> "SUCCESS";
            case "24" -> "CANCELLED";
            default -> "FAILED";
        };

        // Fast path: cache then async DB persist
        cache.putStatus(txnRef, status, status.equals("SUCCESS") ? Duration.ofHours(24) : Duration.ofHours(1));

        // TODO: persist PaymentTransaction + update PaymentRequest

        return Map.of("RspCode", "00", "Message", "Confirm Success");
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
}
