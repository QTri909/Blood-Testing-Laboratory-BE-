package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reagent_used")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReagentUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reagent_id", nullable = false)
    private Long reagentId; // ID  InventoryService

    @Column(name = "slot_number")
    private String slotNumber;

    @Column(name = "used_volume", precision = 8)
    private Double usedVolume;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}