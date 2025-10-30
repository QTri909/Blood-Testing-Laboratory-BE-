package sum25.group03.payment_service.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VNPayCreatePaymentRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String orderCode;
    @Min(1)
    private long amount; // in VND
    private String orderInfo;
    private String bankCode;
    private String locale = "vn";
    private boolean generateQRCode = false;
    private Integer qrWidth;
    private Integer qrHeight;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getOrderInfo() { return orderInfo; }
    public void setOrderInfo(String orderInfo) { this.orderInfo = orderInfo; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public boolean isGenerateQRCode() { return generateQRCode; }
    public void setGenerateQRCode(boolean generateQRCode) { this.generateQRCode = generateQRCode; }
    public Integer getQrWidth() { return qrWidth; }
    public void setQrWidth(Integer qrWidth) { this.qrWidth = qrWidth; }
    public Integer getQrHeight() { return qrHeight; }
    public void setQrHeight(Integer qrHeight) { this.qrHeight = qrHeight; }
}
