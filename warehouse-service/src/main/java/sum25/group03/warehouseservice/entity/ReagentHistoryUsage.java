package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reagent_history_usage")
public class ReagentHistoryUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reagent_history_usage_id")
    private Long reagentHistoryUsageId;

    @Column(name = "quantity_used", nullable = false)
    private long quantityUsed;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "usage_type", nullable = false)
    private String usageType;

    @Column(name = "test_order_id", nullable = false)
    private long testOrderId;

    @Column(name = "used_by", nullable = false)
    private int usedBy;

    @Column(name = "used_at", nullable = false)
    private LocalDate usedAt;

    @Column(name = "notes", nullable = false, columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;
}
