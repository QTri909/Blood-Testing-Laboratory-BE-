package sum25.group03.testorderservice.dtos.response;

import lombok.*;
import sum25.group03.testorderservice.enums.CommentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private Long testOrderId;
    private Long testResultId;
    private Long userId;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentStatus status;

    private GrpcUserInfo creatorInfo;
}
