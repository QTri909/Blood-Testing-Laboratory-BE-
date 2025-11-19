package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import sum25.group03.warehouseservice.dto.response.ReagentRes;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReagentSupplyReq {
    @NotNull
    private Long vendorId;
    @NotNull
    private List<SupplyReq> supplyReq;
}
