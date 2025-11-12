package sum25.group03.testorderservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import sum25.group03.testorderservice.enums.TestOrderStatus;
import sum25.group03.testorderservice.dtos.response.TestResultResponseDTO;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestOrderResponseDTO {
    private Long id;
    private Long externalMedicalRecordId;
    private Long patientId;
    private Long createdBy;
    private Long runBy;
    private String code;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDate runDate;
    private TestOrderStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDateTime updatedAt;
    private List<TestResultResponseDTO> testResults;
    private List<CommentResponseDTO> comments;
    private String barcode;
    private Long totalPrice;
}
