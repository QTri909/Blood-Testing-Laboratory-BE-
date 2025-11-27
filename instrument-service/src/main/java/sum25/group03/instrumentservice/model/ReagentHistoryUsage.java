package sum25.group03.instrumentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
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

    @Column(name = "lot_reagent_id")
    private Integer lotReagentId;

    @Column(name = "lot_number")
    private String lotNumber;

    @Column(name = "volume_used")
    private Double volumeUsed;

    @Column(name = "unit")
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type")
    private UsageType usageType;

    @Column(name = "test_order_id")
    private Long testOrderId;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @Column(name = "reagent_id")
    private Long reagentId;

    @Column(name = "reagent_name")
    private String reagentName;

    @Column(name = "used_by")
    private Integer usedBy;

    @Column(name = "used_at")
    @CreationTimestamp
    private LocalDateTime usedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}