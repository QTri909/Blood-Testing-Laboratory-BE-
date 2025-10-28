package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistorySupplyRes {
    private VendorRes vendor;
    private List<SupplyRes> supply;
}
