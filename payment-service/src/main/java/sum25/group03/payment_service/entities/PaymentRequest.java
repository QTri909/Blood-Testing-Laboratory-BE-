package sum25.group03.payment_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import sum25.group03.payment_service.enums.PaymentRequestStatus;
import sum25.group03.payment_service.enums.StandardCurrency;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest implements Serializable {

    @Id
    @UuidGenerator
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id;

    @Column(name = "order_code", nullable = false)
    private String orderCode; // reference to the order being paid

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private StandardCurrency currency;

    @Enumerated(EnumType.STRING)
    private PaymentRequestStatus status;

    // External-facing transaction reference (VNPay txnRef)
    @Column(name = "txn_ref", unique = true)
    private String txnRef;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_provider_id", nullable = false)
    private PaymentProvider paymentProvider;

    @PrePersist
    public void prePersist() {
        if (this.status == null)
            this.status = PaymentRequestStatus.PENDING;
    }
}
