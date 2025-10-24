package sum25.group03.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalInstrumentStatusResponse {
    private String instrumentName;
    private String instrumentModel;
    private String currentStatus;
    private String message;
    private LocalDate checkedAt;

}
