package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentStatusResponse {
    private Long instrumentId;
    private String instrumentName;
    private OperationalStatus status;
    private String message;
    private LocalDate checkedAt;
}
