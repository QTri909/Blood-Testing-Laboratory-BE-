package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sum25.group03.warehouseservice.entity.enums.ReagentStatus;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "installed_reagents")
public class InstalledReagent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installed_reagent_id")
    private Long installedReagentId;

    @Column(name = "reagent_name", nullable = false)
    private int currentQuantity;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReagentStatus status;
    @Column(name = "installed_at", nullable = false)
    @CreationTimestamp
    private LocalDate installedAt;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagent_id", nullable = false)
    private Reagents reagent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;
}
