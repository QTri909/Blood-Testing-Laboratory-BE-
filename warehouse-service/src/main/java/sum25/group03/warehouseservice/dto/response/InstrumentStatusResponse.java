package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentStatusResponse {
    private String instrumentName;
    private String instrumentModel;
    private String currentStatus;
    private String message;
    private LocalDate checkedAt;
}
