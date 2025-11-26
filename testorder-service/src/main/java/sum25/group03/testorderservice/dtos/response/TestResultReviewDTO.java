package sum25.group03.testorderservice.dtos.response;

import lombok.Data;

import java.util.List;

@Data
public class TestResultReviewDTO {
    private List<String> abnormalities;
    private String severity;
    private String summary;
    private String recommendation;
}
