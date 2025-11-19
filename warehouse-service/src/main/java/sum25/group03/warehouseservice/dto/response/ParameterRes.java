package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParameterRes {
    private Long id;
    private String parameterName;
    private String abbreviation;
    private String description;

    private String unit;
    private String gender;
    private Double price;

    private String status;

    // normal range
    private String normalRange;
    private Double minValue;
    private Double maxValue;
}
