package sum25.group03.payment_service.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import sum25.group03.payment_service.enums.PaymentTransactionStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentTransaction implements Serializable {
    @Id
    @UuidGenerator
    private UUID id;

    // 1 Payment request has many payment transaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_request_id",
            nullable = false
    )
    private PaymentRequest paymentRequest;

    @Column(name = "gateway_transaction_id", nullable = false)
    private String gatewayTransactionId;    // external transaction id (from vnpay, paypal,..)

    @Enumerated(EnumType.STRING)
    private PaymentTransactionStatus status;

    @Column(name = "gateway_status_code", length = 50)
    private String gatewayStatusCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "raw_response")
    private Map<String, Object> rawResponse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (this.status == null)
            this.status = PaymentTransactionStatus.PENDING;
    }

    public PaymentTransaction(PaymentRequest paymentRequest, String gatewayTransactionId, PaymentTransactionStatus status, Map<String, Object> rawResponse, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.paymentRequest = paymentRequest;
        this.gatewayTransactionId = gatewayTransactionId;
        this.status = status;
        this.rawResponse = rawResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
