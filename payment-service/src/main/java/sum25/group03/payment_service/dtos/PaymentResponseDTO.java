package sum25.group03.payment_service.dtos;

public class PaymentResponseDTO {
    private Long paymentRequestId;
    private String orderCode;
    private String status;
    private String message;
    private Long amount;
    private String transactionId;
    private String dataSource; // REDIS | DATABASE | VNPAY_API | VALIDATION
    private String txnRef;

    public Long getPaymentRequestId() { return paymentRequestId; }
    public void setPaymentRequestId(Long paymentRequestId) { this.paymentRequestId = paymentRequestId; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getTxnRef() { return txnRef; }
    public void setTxnRef(String txnRef) { this.txnRef = txnRef; }
}
