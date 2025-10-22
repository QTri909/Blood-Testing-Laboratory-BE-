package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @Column(name = "iot_number", nullable = false)
    private String lotNumber;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_received", nullable = false)
    private BigDecimal quantityReceived;

    @Column(name = "unit_of_measurement", nullable = false)
    private String unitOfMeasurement;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "received_by", nullable = false)
    private int receivedBy;

    @Column(name = "storage_location", nullable = false)
    private String storageLocation;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SupplyStatus status;

    @Column(name = "notes", nullable = false)
    private String notes;

    @Column(name = "quality_check_status", nullable = false)
    private String qualityCheckStatus;

    @Column(name = "quality_check_date", nullable = false)
    private LocalDate qualityCheckDate;

    @Column(name = "quality_checked_by", nullable = false)
    private int qualityCheckedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "purchase_order_number", nullable = false)
    private String purchaseOrderNumber;

    @Column(name = "manufacturer", nullable = true)
    private String manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

}
