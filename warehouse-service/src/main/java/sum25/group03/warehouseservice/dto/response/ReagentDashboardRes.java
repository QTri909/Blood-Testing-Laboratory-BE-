package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReagentDashboardRes {
    private int totalReagents;
    private int lowStockReagents;
    private int expiringSoonLots;
    private double todayUsage;
    private List<TopUsedReagentsRes> topUsedReagents;
}
