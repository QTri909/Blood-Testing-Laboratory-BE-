package sum25.group03.testorderservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.enums.TestOrderType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "test_order")
@Builder
public class TestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID code;

    @Pattern(regexp = "^BC-\\d{6}$", message = "Order number must follow the pattern 'BC-XXXXXX' where X is a digit.")
    @Column(name = "barcode", nullable = false, unique = true)
    private String barcode;

    @Enumerated(EnumType.STRING)
    private TestOrderType type;

    @Column(name = "external_medical_record_id", nullable = false)
    private Long externalMedicalRecordId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // User Id ( IAMService)

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // user ID ( IAMService)

    @Column(name = "run_by")
    private Long runBy; // user ID ( IAMService)

    @Column(name = "run_date")
    private LocalDate runDate;

    @Enumerated(EnumType.STRING)
    private TestOrderStatus status;

    @Column(name = "instrument_id")
    private Long instrumentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL)
    private List<TestResult> testResults;

    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @PrePersist
    private void prePersist() {
        if (this.status == null)
            this.status = TestOrderStatus.WAITING_PAYMENT;
    }
}
