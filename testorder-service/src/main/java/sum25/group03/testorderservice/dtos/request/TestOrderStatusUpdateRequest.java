package sum25.group03.testorderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderStatus;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class TestOrderStatusUpdateRequest implements Serializable {
    @NotNull
    private TestOrderStatus newStatus;
}
