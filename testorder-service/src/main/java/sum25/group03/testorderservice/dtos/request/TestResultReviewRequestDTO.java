package sum25.group03.testorderservice.dtos.request;

import lombok.Data;

@Data
public class TestResultReviewRequestDTO {
    private Long testResultId;
    private String abnormalities;
    private String severity;
    private String summary;
    private String recommendation;
    private String review;
}
