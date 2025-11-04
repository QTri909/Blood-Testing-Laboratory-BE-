package sum25.group03.testorderservice.dtos.response;

import lombok.*;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestOrderResponseForInstrument {
    private Long id;

    private Long externalMedicalRecordId;

    private UUID code;

    private Long patientId;

    private Long createdBy;

    private Long runBy;

    private String barcode;

    private String testType;


    private LocalDate runDate;

    private TestOrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
