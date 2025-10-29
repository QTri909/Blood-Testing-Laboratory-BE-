package sum25.group03.instrumentservice.client.response;

import lombok.*;

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

    private String code;

    private Long patientId;

    private Long createdBy;

    private Long runBy;

    private String barcode;

    private String testType;


    private LocalDate runDate;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
