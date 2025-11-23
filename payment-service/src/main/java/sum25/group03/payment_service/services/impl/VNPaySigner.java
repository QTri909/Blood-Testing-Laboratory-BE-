package sum25.group03.payment_service.services.impl;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public final class VNPaySigner {
    private VNPaySigner() {}

    public static String canonicalQuery(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + encodeVnPay(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * VNPay-compatible URL encoding:
     * - encodeURIComponent-style encoding
     * - Spaces MUST be converted to '+'
     * - NOT %20
     */
    private static String encodeVnPay(String value) {
        try {
            // URLEncoder.encode converts ' ' → '+', which is EXACTLY what VNPay requires
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);

            // However, URLEncoder encodes '*' and '~' incorrectly for VNPay
            // VNPay wants encodeURIComponent behavior:
            encoded = encoded.replace("%21", "!")
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%7E", "~");

            return encoded;
        } catch (Exception e) {
            throw new RuntimeException("VNPay encoding failed", e);
        }
    }



    public static String hmacSHA512(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compute HMAC", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static String urlEncode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
