package sum25.group03.testorderservice.dtos.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CleanTestResultResponse implements Serializable {
    private Long testResultId;
    private Long parameterId;
    private String parameterCode;
}
