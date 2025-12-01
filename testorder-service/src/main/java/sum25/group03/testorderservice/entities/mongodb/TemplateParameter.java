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
    private String paramName;
    private String paramDesc;
    private String price;
}
