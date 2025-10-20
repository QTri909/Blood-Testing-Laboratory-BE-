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

    @Column(name = "quantity_used", nullable = true)
    private long quantityUsed;

    @Column(name = "unit", nullable = true)
    private String unit;

    @Column(name = "usage_type", nullable = true)
    private String usageType;

    @Column(name = "test_order_id", nullable = true)
    private long testOrderId;

    @Column(name = "used_by", nullable = true)
    private int usedBy;

    @Column(name = "used_at", nullable = true)
    private LocalDate usedAt;

    @Column(name = "notes", nullable = true, columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;
}
