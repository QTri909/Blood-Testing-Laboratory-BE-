package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NormalRangeRes {
    private Double minValue;
    private Double maxValue;
    private  String unit;
    private String gender;
}
