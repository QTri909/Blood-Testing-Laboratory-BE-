package sum25.group03.warehouseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnusedTestParameterRes {
    private Long testOrderId;
    private List<TestParameterRes> unusedTestParameters;
}
