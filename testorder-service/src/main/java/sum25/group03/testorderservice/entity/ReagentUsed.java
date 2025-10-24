package sum25.group03.testorderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sum25.group03.testorderservice.enums.ReagentStatus;

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

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}