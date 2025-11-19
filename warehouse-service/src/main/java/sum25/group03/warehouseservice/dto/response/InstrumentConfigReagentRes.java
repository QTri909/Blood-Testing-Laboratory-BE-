package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstrumentConfigReagentRes {
    private Long instrumentId;
    private String instrumentName;
    private String model;
    private String serialNumber;
    //private String location;
    private String notes;
    private InstrumentStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private ConfigRes configRes;
    private List<ReagentForInstrumentRes> reagentForInstrumentRes;
}
