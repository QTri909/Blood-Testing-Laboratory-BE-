package sum25.group03.testorderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.testorderservice.enums.TestResultStatus;
import sum25.group03.testorderservice.enums.TestType;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
        @NotNull
        private Long id;

        private Long testOrderId;

        private Long instrumentId;

        private Long parameterSnapshotId;

        private String flagStatus;

        private TestResultStatus status;

        private Double value;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private TestType testType;

        private Long parameterId;

        private List<Long> reagentUsedIds;

        private List<Long> commentIds;
}
