package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reagent_history_supply")
public class ReagentHistorySupply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reagent_history_supply_id")
    private Long reagentHistorySupplyId;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "iot_number", nullable = false)
    private String iotNumber;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_received", nullable = false)
    private BigDecimal quantityReceived;

    @Column(name = "quantity_remaining", nullable = false)
    private BigDecimal quantityRemaining;

    @Column(name = "unit_of_measurement", nullable = false)
    private String unitOfMeasurement;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "received_by", nullable = false)
    private int receivedBy;

    @Column(name = "storage_location", nullable = false)
    private String storageLocation;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes", nullable = false)
    private String notes;

    @Column(name = "quality_check_status", nullable = false)
    private String qualityCheckStatus;

    @Column(name = "quality_check_date", nullable = false)
    private LocalDate qualityCheckDate;

    @Column(name = "quality_checked_by", nullable = false)
    private int qualityCheckedBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "purchase_order_number", nullable = false)
    private String purchaseOrderNumber;

    @Column(name = "catalog_number", nullable = false)
    private String catalogNumber;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;
}
