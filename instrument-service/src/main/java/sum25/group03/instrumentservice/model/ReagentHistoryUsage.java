package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.instrumentservice.common.UsageType;

import java.time.LocalDateTime;

@Entity
@Table(name = "reagent_history_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentHistoryUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reagent_batch_id")
    private Integer reagentBatchId;

    @Column(name = "volume_used")
    private Double volumeUsed;

    @Column(name = "unit")
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type")
    private UsageType usageType;

    @Column(name = "test_order_id")
    private Integer testOrderId;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(name = "used_by")
    private Integer usedBy;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}