package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.enums.TestType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_result")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_order_id", nullable = false)
    private TestOrder testOrder;

    @Column(name = "instrument_id", nullable = false)
    private Long instrumentId; // ID InstrumentService

    @Column(name = "parameter_latest_snapshot_id", nullable = false)
    private Long parameterSnapshotId; // ID ParameterService

    @Column(name = "flag_status", nullable = false)
    private String flagStatus;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TestResultStatus status;

    @Column(nullable = false)
    private Double value;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "test_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TestType testType;

    @ManyToOne(
            fetch = FetchType.LAZY, // Lazy loading for better performance
            optional = false // Make the relationship mandatory
    )
    @JoinColumn(name = "parameter_id", nullable = false)
    private Parameter parameter;

    @ManyToMany
    @JoinTable(
            name = "test_result_reagent_used",
            joinColumns = @JoinColumn(name = "test_result_id"),
            inverseJoinColumns = @JoinColumn(name = "reagent_used_id")
    )
    private List<ReagentUsed> reagentsUsed;

    @OneToMany(mappedBy = "testResult")
    private List<Comment> comments;

    // 1 synced configuration can be associated with many test results
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuration_id")
    private SyncedConfiguration syncedConfiguration;
}