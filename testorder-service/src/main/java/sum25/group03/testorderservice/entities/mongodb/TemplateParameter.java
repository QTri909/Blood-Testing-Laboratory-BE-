package sum25.group03.testorderservice.entities.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TemplateParameter {
    private String paramCode;
    private String name;
    private String description;
    private String price;
    private Long externalId;
    private String abbreviation;
    private Double min;;
    private Double max;
    private String unit;
    private String status;;
    private String gender;
}