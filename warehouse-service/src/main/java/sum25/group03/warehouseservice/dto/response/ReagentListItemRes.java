package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReagentListItemRes {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private double totalStock;
    private double maxStockLevel;
    private double lowStockLevel;
    private String unit;
}
