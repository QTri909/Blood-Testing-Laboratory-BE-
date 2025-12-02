package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ReagentInventoryStatus;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReagentInventoryRes {
    private Long reagentInventoryId;
    private String lotNumber;
    private double quantityAvailable;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiryDate;
    private ReagentInventoryStatus status;
}
