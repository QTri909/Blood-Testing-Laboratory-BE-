package sum25.group03.testorderservice.dtos.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class CleanTestOrderResponse implements Serializable {
    private Long testOrderId;
    private String barcode;
    private List<CleanTestResultResponse> testResults;
}
