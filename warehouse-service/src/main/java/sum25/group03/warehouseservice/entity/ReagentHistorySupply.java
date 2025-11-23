package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.warehouseservice.entity.enums.ReagentUnit;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_received", nullable = false)
    private double quantityReceived;

    @Column(name = "unit_of_measurement", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentUnit unitOfMeasurement;


//    @Column(name = "received_date", nullable = false)
//    private LocalDate receivedDate;

    @Column(name = "received_by", nullable = true)
    private int receivedBy;

//    @Column(name = "status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private SupplyStatus status;

    @Column(name = "notes", nullable = true)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "batch_code", nullable = false)
    private UUID batchCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

}
