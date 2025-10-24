package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
            this.status = TestOrderStatus.PENDING;
    }
}
