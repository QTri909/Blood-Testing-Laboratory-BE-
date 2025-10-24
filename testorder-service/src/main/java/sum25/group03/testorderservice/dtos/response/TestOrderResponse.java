package sum25.group03.testorderservice.dtos.response;

import lombok.*;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestOrderResponse{

    private Long id;

    private Long externalMedicalRecordId;

    private Long patientId;

    private Long createdBy;

    private Long runBy;

    private LocalDate runDate;

    private TestOrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Nested
    private List<TestResultResponse> testResults;

    private List<CommentResponse> comments;
}
