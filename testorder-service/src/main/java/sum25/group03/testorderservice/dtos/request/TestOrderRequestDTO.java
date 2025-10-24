package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}