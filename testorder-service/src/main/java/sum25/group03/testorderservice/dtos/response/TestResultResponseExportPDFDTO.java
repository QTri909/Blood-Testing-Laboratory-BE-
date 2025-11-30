package sum25.group03.testorderservice.dtos.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.FlagStatus;
import sum25.group03.testorderservice.enums.TestResultStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class TestResultResponseExportPDFDTO {
    private Long id;
    private String parameterName;
    private Double value;
    private FlagStatus flagStatus;
    private TestResultStatus status;
    private LocalDateTime createdAt;
//    private List<CommentResponseDTO> comments;
    private String review;
    // Optional helper cho Jasper (tránh null)
    public String getReviewText() {
        return review == null ? "" : review;
    }

    /**
     * Trả về tất cả comment của TestResult nối bằng ", "
     */
//    public String getCommentTexts() {
//        if (comments == null || comments.isEmpty()) return "";
//        return comments.stream()
//                .map(CommentResponseDTO::getCommentText)
//                .filter(c -> c != null && !c.isBlank())
//                .collect(Collectors.joining(", "));
//    }


}
