package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.SupplyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplyRes {
    private double quantityReceived;
    private String lotNumber;
    private String unitOfMeasurement;
    private LocalDate receivedDate;
    private int receivedBy;
    private LocalDate expiryDate;
    private LocalDate manufactureDate;
    private SupplyStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private ReagentRes reagentRes;
}
