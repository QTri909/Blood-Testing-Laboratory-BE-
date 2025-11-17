package sum25.group03.warehouseservice.dto.response;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ParameterStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GlobalTestParameterRes {
    private Long globalTestParameterId;
    private Long id;
    private String abbreviation;
    private String parameterName;
    private Double price;
    private String description;
    private List<NormalRangeRes> normalRange;
}
