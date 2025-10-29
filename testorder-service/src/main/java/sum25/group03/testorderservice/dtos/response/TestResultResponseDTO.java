package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.enums.TestType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultResponseDTO {
    private Long id;
    private Long testOrderId;
    private Long instrumentId;
    private Long parameterSnapshotId;
    private Long parameterId;
    private String parameterName;
    private String flagStatus;
    private TestResultStatus status;
    private Double value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TestType testType;
    private List<ReagentUsedResponseDTO> reagentsUsed;
    private List<CommentResponseDTO> comments;
}
