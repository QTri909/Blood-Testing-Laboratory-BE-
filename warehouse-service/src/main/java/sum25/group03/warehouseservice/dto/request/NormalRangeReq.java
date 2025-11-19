package sum25.group03.warehouseservice.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.Gender;
import sum25.group03.warehouseservice.entity.enums.ParamUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NormalRangeReq {
    @NotNull
    private Double minValue;
    @NotNull
    private Double maxValue;
    @NotNull
    private ParamUnit unit;
    @NotNull
    private Gender gender;

}
