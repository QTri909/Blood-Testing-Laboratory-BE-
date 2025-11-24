package sum25.group03.warehouseservice.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReagentCreatedEvent {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private String casNumber;
}
