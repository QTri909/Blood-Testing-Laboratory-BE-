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
public class ReagentDetailRes {
    private Long reagentId;
//    private String reagentName;
//    private String catalogNumber;
    private String description;
//    private double maxStockLevel;
//    private double totalStock;
    
    private List<ReagentInventoryRes> inventories;

}
