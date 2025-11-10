package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^BC-\\d{6}$", message = "Order number must follow the pattern 'BC-XXXXXX' where X is a digit.")
    private String barcode;
    private String type;
}
