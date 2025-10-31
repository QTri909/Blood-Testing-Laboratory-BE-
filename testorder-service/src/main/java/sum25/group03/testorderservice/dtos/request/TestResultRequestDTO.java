package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultRequestDTO {
    @NotNull(message = "Test order ID cannot be null")
    private Long testOrderId;

    @NotNull(message = "Instrument ID cannot be null")
    private Long instrumentId;

    @NotNull(message = "Parameter snapshot ID cannot be null")
    private Long parameterSnapshotId;

    @NotNull(message = "Parameter ID cannot be null")
    private Long parameterId;

    @NotBlank(message = "Flag status cannot be blank")
    private String flagStatus;

    @NotNull(message = "Value cannot be null")
    private Double value;

    @NotNull(message = "Test type cannot be null")
    private TestType testType;

    private List<Long> reagentUsedIds;
}