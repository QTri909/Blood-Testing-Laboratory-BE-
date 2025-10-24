package sum25.group03.testorderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long testOrderId;
    private Long testResultId;
    private Long userId;
    private String commentText;
    private CommentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
