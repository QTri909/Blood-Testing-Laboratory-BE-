package sum25.group03.payment_service.dtos;

public class VNPayCreatePaymentResponse {
    private String paymentUrl;
    private String txnRef;
    private Long paymentRequestId;
    private Long amount;
    private String qrCodeBase64;
    private String qrCodeDataUrl;
    private boolean cachedInRedis;
    private Long cacheTTL;

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getTxnRef() { return txnRef; }
    public void setTxnRef(String txnRef) { this.txnRef = txnRef; }
    public Long getPaymentRequestId() { return paymentRequestId; }
    public void setPaymentRequestId(Long paymentRequestId) { this.paymentRequestId = paymentRequestId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
    public String getQrCodeDataUrl() { return qrCodeDataUrl; }
    public void setQrCodeDataUrl(String qrCodeDataUrl) { this.qrCodeDataUrl = qrCodeDataUrl; }
    public boolean isCachedInRedis() { return cachedInRedis; }
    public void setCachedInRedis(boolean cachedInRedis) { this.cachedInRedis = cachedInRedis; }
    public Long getCacheTTL() { return cacheTTL; }
    public void setCacheTTL(Long cacheTTL) { this.cacheTTL = cacheTTL; }
}
