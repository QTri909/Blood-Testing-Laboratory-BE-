package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ParameterStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestParameterReq {
    @NotBlank
    private String parameterName;
    private String description;
    @NotBlank
    private String abbreviation;
    @NotBlank
    private Double price;
    List<NormalRangeReq> normalRange;
}
