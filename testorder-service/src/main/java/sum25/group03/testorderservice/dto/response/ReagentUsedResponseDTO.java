package sum25.group03.testorderservice.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUsedResponseDTO {
    private Long id;
    private Long reagentId;
    private String slotNumber;
    private Integer quantity;
    private LocalDateTime usedAt;
    private LocalDateTime updatedAt;
}
