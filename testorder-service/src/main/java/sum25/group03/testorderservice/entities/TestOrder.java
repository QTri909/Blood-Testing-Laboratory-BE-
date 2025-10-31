package sum25.group03.testorderservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import sum25.group03.testorderservice.enums.TestOrderStatus; // Giả sử bạn có enum này

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
    private String code;

    @Column(name = "external_medical_record_id", nullable = true)
    private Long externalMedicalRecordId;

    @Column(name = "patient_id", nullable = true)
    private Long patientId;

    @Column(name = "created_by")
    private Long createdBy;

    @Pattern(regexp = "^BC-\\d{6}$",
            message = "Barcode phải có định dạng BC-123456 (ví dụ: BC-987654)")
    @Column(name= "barcode")
    private String barcode;


    @Column(name = "run_by")
    private Long runBy;

    @Column(name = "run_date")
    private LocalDate runDate;

    @Enumerated(EnumType.STRING)
    private TestOrderStatus status;

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
        if (this.status == null) {
            if (this.patientId == null) {
                this.status = TestOrderStatus.UNMATCHED;
            } else {
                this.status = TestOrderStatus.PENDING;
            }
        }
    }
}