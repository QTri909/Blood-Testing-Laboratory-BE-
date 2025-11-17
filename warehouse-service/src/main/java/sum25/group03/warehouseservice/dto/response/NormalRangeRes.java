package sum25.group03.warehouseservice.dto.response;

import lombok.*;
import sum25.group03.warehouseservice.entity.enums.Gender;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NormalRangeRes {
    private Double minValue;
    private Double maxValue;
    private  String unit;
    private Gender gender;
}
