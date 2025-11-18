package sum25.group03.common.response.dtos.grpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CleanTestOrderResponse implements Serializable {
    private Long testOrderId;
    private String barcode;
    private List<CleanTestResultResponse> testResults;
}
