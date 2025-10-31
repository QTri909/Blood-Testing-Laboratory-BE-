package sum25.group03.patientservice.grpc.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrpcTestOrderDTO {
    private Long id;
    private String barcode;
    private String type;
    private Long patientId;
    private String status;
    private String createdAt;
}