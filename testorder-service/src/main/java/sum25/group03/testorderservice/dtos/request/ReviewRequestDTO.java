package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    @NotNull(message = "Test Result Id is required")
    private Long testResultId;

    @NotNull(message = "Review is required")
    private String review;
}
