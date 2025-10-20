package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "raw_test_results")
public class RawTestResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raw_test_result_id")
    private Long rawTestResultId;

    @Column(name = "test_order_id", nullable = false)
    private Long testOrderId;
    @Column(name = "raw_data", nullable = false, columnDefinition = "TEXT")
    private String rawData;
    @Column(name = "hl7_message", nullable = false, columnDefinition = "TEXT")
    private String hl7Message;
    @Column(name = "is_sent_to_monitoring", nullable = false)
    private boolean sentToMonitoring;
    @Column(name = "is_synced", nullable = false)
    private boolean synced;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @OneToMany(mappedBy = "rawTestResults", cascade =  CascadeType.ALL)
    private List<Instrument> instrument;
}
