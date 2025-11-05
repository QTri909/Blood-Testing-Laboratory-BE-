package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderRequestDTO {
    @NotNull(message = "External medical record ID cannot be null")
    private Long externalMedicalRecordId;

    @NotNull(message = "Patient ID cannot be null")
    private Long patientId;

    @NotNull(message = "Created by cannot be null")
    private Long createdBy;

    private Long runBy;
    private LocalDate runDate;
    private TestOrderStatus status ;

    @Pattern(regexp = "^BC-\\d{6}$", message = "Order number must follow the pattern 'BC-XXXXXX' where X is a digit.")
    private String barcode;
    private String type;
}