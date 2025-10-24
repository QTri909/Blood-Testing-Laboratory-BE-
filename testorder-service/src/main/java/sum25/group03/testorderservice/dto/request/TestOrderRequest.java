package sum25.group03.testorderservice.dto.request;

import lombok.*;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestOrderRequest {

    private Long externalMedicalRecordId;
    private Long patientId;
    private Long createdBy;
    private Long runBy;
    private LocalDate runDate;
    private TestOrderStatus status;
}
