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

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "storage_conditions", nullable = false)
    private String storageConditions;

    @Column(name = "min_stock_level", nullable = false)
    private int minStockLevel;

    @Column(name = "max_stock_level", nullable = false)
    private int maxStockLevel;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistorySupply> reagentHistorySupplies;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentInventory> reagentInventories;
}
