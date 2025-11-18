package sum25.group03.common.response.dtos.grpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CleanTestResultResponse implements Serializable {
    private Long testResultId;
    private Long parameterId;
    private String parameterCode;
}
