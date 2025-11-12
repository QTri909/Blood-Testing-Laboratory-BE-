package sum25.group03.patientservice.grpc.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GrpcTestOrderFullFieldDTO implements Serializable {
    private Long id;
    private Long externalMedicalRecordId;
    private Long patientId;
    private Long createdBy;
    private Long runBy;
    private String code;
    private String runDate;
    private String status;
    private String createdAt;
    private String updatedAt;
    private java.util.List<GrpcTestResultDTO> testResults;
    private java.util.List<GrpcCommentDTO> comments;
    private String barcode;
    private Long totalPrice;
}
