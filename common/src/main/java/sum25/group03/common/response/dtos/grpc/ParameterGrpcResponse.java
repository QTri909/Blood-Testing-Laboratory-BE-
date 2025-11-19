package sum25.group03.common.response.dtos.grpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterGrpcResponse {
    private Boolean success;
    private String message;
}
