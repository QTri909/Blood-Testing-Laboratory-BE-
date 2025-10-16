package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "reagent_code", nullable = false)
    private String reagentCode;

    @Column(name = "reagent_name", nullable = false)
    private String reagentName;

    @Column(name = "catalog_number", nullable = false)
    private String catalogNumber;

    @Column(name = "cas_number", nullable = false)
    private String casNumber;

    @Column(name = "iot_number", nullable = false)
    private String iotNumber;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "storage_temperature", nullable = false)
    private String storageTemperature;

    @Column(name = "status", nullable = false)
    private String status;

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

    @Column(name = "total_quantity_remaining", nullable = false)
    private BigDecimal totalQuantityRemaining;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistoryUsage> reagentHistoryUsages;

    @OneToMany(mappedBy = "reagent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReagentHistorySupply> reagentHistorySupplies;
}
