package sum25.group03.payment_service.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vnpay")
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

    public static class QrCode {
        private boolean enabled = true;
        private int defaultWidth = 300;
        private int defaultHeight = 300;
        private String format = "PNG";
        private String errorCorrectionLevel = "H";
        private int margin = 1;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getDefaultWidth() { return defaultWidth; }
        public void setDefaultWidth(int defaultWidth) { this.defaultWidth = defaultWidth; }
        public int getDefaultHeight() { return defaultHeight; }
        public void setDefaultHeight(int defaultHeight) { this.defaultHeight = defaultHeight; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getErrorCorrectionLevel() { return errorCorrectionLevel; }
        public void setErrorCorrectionLevel(String errorCorrectionLevel) { this.errorCorrectionLevel = errorCorrectionLevel; }
        public int getMargin() { return margin; }
        public void setMargin(int margin) { this.margin = margin; }
    }

    public String getTmnCode() { return tmnCode; }
    public void setTmnCode(String tmnCode) { this.tmnCode = tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public void setHashSecret(String hashSecret) { this.hashSecret = hashSecret; }
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getQueryUrl() { return queryUrl; }
    public void setQueryUrl(String queryUrl) { this.queryUrl = queryUrl; }
    public String getRefundUrl() { return refundUrl; }
    public void setRefundUrl(String refundUrl) { this.refundUrl = refundUrl; }
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    public String getIpnUrl() { return ipnUrl; }
    public void setIpnUrl(String ipnUrl) { this.ipnUrl = ipnUrl; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public QrCode getQrCode() { return qrCode; }
    public void setQrCode(QrCode qrCode) { this.qrCode = qrCode; }
}
