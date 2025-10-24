package sum25.group03.testorderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    @NotNull(message = "Test order ID cannot be null")
    private Long testOrderId;

    private Long testResultId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Comment text cannot be blank")
    private String commentText;
}
