package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;
import sum25.group03.warehouseservice.entity.enums.ReagentUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentUnit unit;

    @Column(name = "storage_conditions", nullable = false)
    private String storageConditions;

    @Column(name = "max_stock_level", nullable = false, columnDefinition = "integer default 2000")
    private Integer maxStockLevel;

    @Column(name = "min_stock_level", nullable = false, columnDefinition = "integer default 0")
    private Integer minStockLevel;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "usage_min")
    private Double usageMin;

    @Column(name = "usage_max")
    private Double usageMax;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistorySupply> reagentHistorySupplies;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentInventory> reagentInventories;
}
