package sum25.group03.patientservice.dtos.request;

import jakarta.validation.constraints.NotNull;

public record MedicalRecordRequest(
        @NotNull Long patientId,
        @NotNull Long assignedUser,
        @NotNull Long createdBy,
        @NotNull Long updatedBy
) {
}
