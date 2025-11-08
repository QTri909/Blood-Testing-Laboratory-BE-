package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultRequestDTO {
    @NotNull(message = "Test order ID cannot be null")
    private Long testOrderId;

    @NotNull(message = "Parameter ID cannot be null")
    private Long parameterId;
}