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
                .map(e -> e.getKey() + "=" + urlEncodeVNPay(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * VNPay-compatible URL encoding:
     * - Spaces -> %20 (not +)
     * - UTF-8 encoding for special characters
     */
    private static String urlEncodeVNPay(String value) {
        // Standard URLEncoder.encode turns spaces into '+', VNPay expects '%20'
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")   // fix spaces
                .replace("*", "%2A")   // optional
                .replace("%7E", "~");  // optional
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
