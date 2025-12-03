package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReagentHistoryUsageOfInstrumentRes {
    private String reagentName;
    private double quantityUsed;
    private String unit;
    private String lotNumber;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime usedAt;
}
