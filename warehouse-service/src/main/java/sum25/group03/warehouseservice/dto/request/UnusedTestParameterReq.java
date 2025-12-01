package sum25.group03.warehouseservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UnusedTestParameterReq {
    private Long testOrderId;
    private Long globalTestParameterId;
    private List<CurrentAbbrParam> currentAbbrParams;
}