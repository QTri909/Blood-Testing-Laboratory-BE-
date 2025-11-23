package sum25.group03.common.response.dtos.grpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ParameterGrpc {
    private Long id;
    private String abbreviation;
    private String parameterName;
    private Double price;
    private String description;
    private Double minValue;
    private Double maxValue;
    private String unit;
    private String gender;
}
