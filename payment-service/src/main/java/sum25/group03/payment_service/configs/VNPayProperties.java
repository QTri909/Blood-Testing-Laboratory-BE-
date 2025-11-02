package sum25.group03.payment_service.configs;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vnpay")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayProperties {
    private String tmnCode;
    private String hashSecret;
    private String paymentUrl;
    private String queryUrl;
    private String refundUrl;
    private String returnUrl;
    private String ipnUrl;
    private String version = "2.1.0";
    private String command = "pay";
    private String orderType = "other";
    private String locale = "vn";
    private QrCode qrCode = new QrCode();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QrCode {
        private boolean enabled = true;
        private int defaultWidth = 300;
        private int defaultHeight = 300;
        private String format = "PNG";
        private String errorCorrectionLevel = "H";
        private int margin = 1;

    }
}
