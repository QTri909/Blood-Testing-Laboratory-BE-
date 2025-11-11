package sum25.group03.warehouseservice.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParameterRes {
    private Long id;
    private String name;
    private String abbreviation;
    private String description;
    private String range;
    private String status;
}
