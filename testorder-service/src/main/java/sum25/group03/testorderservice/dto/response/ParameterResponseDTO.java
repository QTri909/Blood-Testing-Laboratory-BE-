package sum25.group03.testorderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.ParameterStatus;
import sum25.group03.testorderservice.enums.ParameterUnit;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterResponseDTO {
    private Long id;
    private String paramCode;
    private String name;
    private String abbreviation;
    private String description;
    private Double min;
    private Double max;
    private ParameterUnit unit;
    private ParameterStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
