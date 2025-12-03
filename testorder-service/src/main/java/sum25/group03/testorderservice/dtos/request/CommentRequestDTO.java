package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    @NotNull(message = "Test order ID cannot be null")
    private Long testOrderId;

    private Long testResultId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private String commentText;

    private List<String> abnormalities;

    private String severity;

    private String summary;

    private String recommendation;
}
