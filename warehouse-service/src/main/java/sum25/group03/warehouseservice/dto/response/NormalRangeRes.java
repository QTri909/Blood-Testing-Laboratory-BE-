package sum25.group03.warehouseservice.dto.response;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.Gender;
import sum25.group03.warehouseservice.entity.enums.ParamUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NormalRangeRes {
    private Double minValue;
    private Double maxValue;
    private ParamUnit unit;
    private Gender gender;
}
