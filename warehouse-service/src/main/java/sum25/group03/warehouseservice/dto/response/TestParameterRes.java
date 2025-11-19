package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestParameterRes {
    private Long id;
    private String abbreviation;
    private String parameterName;
    private Double price;
    private String description;
    private List<NormalRangeRes> normalRange;
}
