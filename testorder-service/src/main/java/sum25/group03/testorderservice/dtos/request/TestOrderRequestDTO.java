package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.enums.TestOrderType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderRequestDTO {
    @NotNull(message = "External medical record ID cannot be null")
    private Long externalMedicalRecordId;

    private Long runBy;
    private LocalDate runDate;
    private TestOrderStatus status ;

    @Pattern(regexp = "^BC-\\d{6}$", message = "Order number must follow the pattern 'BC-XXXXXX' where X is a digit.")
    private String barcode;

    @NotNull(message = "Type cannot be null")
    private TestOrderType type;

    private TestOrderPatientInfo patientInfo;
}