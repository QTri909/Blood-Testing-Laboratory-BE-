package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReagentUsageRes {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate useDate;
    private Long instrumentId;
    private String instrumentName;
    private double quantityUsed;
    private String unit;
    private String reagentName;
    private String lotNumber;
}
