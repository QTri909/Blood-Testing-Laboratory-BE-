package sum25.group03.warehouseservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentStatusResponse {

    private Long instrumentId;
    private String instrumentName;
    private String instrumentCode;
    private InstrumentStatus status;
    private String instrumentModel;
    private boolean isActive;
    private String location;
    private String message;

}
