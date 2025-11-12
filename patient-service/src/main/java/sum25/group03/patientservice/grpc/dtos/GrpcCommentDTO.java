package sum25.group03.patientservice.grpc.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrpcCommentDTO implements Serializable {
    private Long id;
    private Long testOrderId;
    private Long testResultId;
    private Long userId;
    private String commentText;
    private String createdAt;
    private String updatedAt;
    private String status;
}
