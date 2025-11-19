package sum25.group03.testorderservice.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.ParameterGender;
import sum25.group03.testorderservice.enums.ParameterStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KafkaParameterRequestDTO {
    /* example:
    id,abbreviation,description,normal_range,parameter_name,global_config_id,max_value,min_value,unit,gender, status
    1,WBC,"Measures the number of white blood cells (leukocytes) in the blood, which helps fight infection.","4,000–10,000 cells/µL",White Blood Cell Count,1,10000,4000,cells/µL,BOTH, ACTIVE
     */

    @JsonProperty("id")
    private Long id;

    @JsonProperty("abbreviation")
    private String paramCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("parameter_name")
    private String name;

    @JsonProperty("max_value")
    private Double max;

    @JsonProperty("min_value")
    private Double min;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("gender")
    private ParameterGender genderParam;

    @JsonProperty("status")
    private ParameterStatus status;
}