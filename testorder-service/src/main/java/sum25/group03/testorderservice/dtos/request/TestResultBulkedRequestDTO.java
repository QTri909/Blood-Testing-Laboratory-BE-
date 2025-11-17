package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
public class TestResultBulkedRequestDTO implements Serializable {

    @NotNull(message = "Test order id is required for inserting new test results")
    private Long testOrderId;

    @NotEmpty(message = "It's required to have at least one parameter for the template")
    private Set<Long> paramsId;

    private Long globalTestParameterId;
}
