package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class TestOrderResponseExportExcelDTO {

    private Long id;
    private String patientName;
    private String gender;
//    private LocalDate dateOfBirth;
    private String phoneNumber;
    private TestOrderStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long runBy;
    private LocalDate runOn;
    private List<TestResultResponseExportPDFDTO> results;
    private String orderComments; // tất cả comment của TestOrder, join bằng String
    public String getOrderComments() {
        return orderComments != null ? orderComments : "";}
}
