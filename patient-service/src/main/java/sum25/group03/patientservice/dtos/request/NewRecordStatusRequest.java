package sum25.group03.patientservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import sum25.group03.patientservice.enums.MedicalRecordStatus;

public record NewRecordStatusRequest (
    @NotNull Long recordId,
    @NotNull MedicalRecordStatus newStatus,
    @NotNull Long updatedBy
) {
}