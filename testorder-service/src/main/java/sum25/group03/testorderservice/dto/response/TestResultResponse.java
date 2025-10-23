package sum25.group03.testorderservice.dto.response;

import lombok.*;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.enums.TestType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultResponse {
    private Long id;
    private Long testOrderId;
    private Long instrumentId;
    private Long parameterSnapshotId;
    private String flagStatus;
    private TestResultStatus testResultStatus;
    private Double value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TestType testType;
    private Long parameterId;
    private List<Long> reagentUsedIds;

}
