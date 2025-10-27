package sum25.group03.warehouseservice.dto.response;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstrumentConfigReagentRes {
    private Long instrumentId;
    private String instrumentName;
    private String model;
    private String serialNumber;
    private String location;
    private String notes;
    private InstrumentStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private GlobalConfigRes globalConfigRes;
    private SpecificConfigRes specificConfigRes;
    private ReagentForInstrumentRes reagentForInstrumentRes;
}
