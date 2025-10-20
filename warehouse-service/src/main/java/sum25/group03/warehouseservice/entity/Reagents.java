package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reagents")
public class Reagents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reagent_id")
    private Long reagentId;

    @Column(name = "reagent_name", nullable = false)
    private String reagentName;

    @Column(name = "catalog_number", nullable = false)
    private String catalogNumber;

    @Column(name = "cas_number", nullable = false)
    private String casNumber;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "storage_conditions", nullable = false)
    private String storageConditions;

    @Column(name = "total_quantity_remaining", nullable = true)
    private BigDecimal totalQuantityRemaining;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentStatus status;

    @Column(name = "in_use", nullable = false)
    private boolean inUse;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistorySupply> reagentHistorySupplies;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstalledReagent> installedReagents;
}
