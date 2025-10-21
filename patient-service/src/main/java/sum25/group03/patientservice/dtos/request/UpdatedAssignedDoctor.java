package sum25.group03.patientservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedAssignedDoctor {
    @NotNull private Long recordId;
    @NotNull private Long assignedUserId;
    @NotNull private Long updatedBy;
}
