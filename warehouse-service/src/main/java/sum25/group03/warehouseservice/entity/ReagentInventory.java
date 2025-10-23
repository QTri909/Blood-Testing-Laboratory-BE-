package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "reagent_inventory")
public class ReagentInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reagent_inventory_id")
    private Long reagentInventoryId;

    @Column(name = "lot_number", nullable = false)
    private String lotNumber;

    @Column(name = "quantity_available", nullable = false)
    private int quantityAvailable;

    @Column(name = "min_stock_level", nullable = false)
    private int minStockLevel;

    @Column(name = "max_stock_level", nullable = false)
    private int maxStockLevel;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;
}
