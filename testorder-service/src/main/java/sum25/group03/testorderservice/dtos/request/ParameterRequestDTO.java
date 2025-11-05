package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.ParameterGender;
import sum25.group03.testorderservice.enums.ParameterUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterRequestDTO {
    @NotBlank(message = "Parameter code cannot be blank")
    private String paramCode;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String abbreviation;

    private String description;

    private Double min;

    private Double max;

    private ParameterGender gender;

    @NotNull(message = "Unit cannot be null")
    private ParameterUnit unit;
}