package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sum25.group03.testorderservice.TestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "test_order")

public class TestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_medical_record_id")
    private Long externalMedicalRecordId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; //PatientService

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // user ID ( UserService)

    @Column(name = "run_by")
    private Long runBy; // user ID ( UserService)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "run_date")
    private LocalDate runDate;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "test_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TestType testType;

    // Relationships
    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL)
    private List<TestResult> testResults;

    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
