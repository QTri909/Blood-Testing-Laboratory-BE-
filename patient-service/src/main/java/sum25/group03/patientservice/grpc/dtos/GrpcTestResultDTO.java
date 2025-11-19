package sum25.group03.patientservice.grpc.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GrpcTestResultDTO implements Serializable {
    private Long id;
    private Long testOrderId;
    private Long instrumentId;
    private Long parameterId;
    private String parameterName;
    private String flagStatus;
    private String status;
    private Double value;
    private String createdAt;
    private String updatedAt;
    private java.util.List<GrpcCommentDTO> comments;
    private Long price;
}
