package sum25.group03.payment_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import sum25.group03.payment_service.enums.PaymentProviderCode;
import sum25.group03.payment_service.enums.PaymentProviderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment_providers")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProvider implements Serializable {

    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false, nullable = false)
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PaymentProviderCode code;

    @Enumerated(EnumType.STRING)
    private PaymentProviderStatus status;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
