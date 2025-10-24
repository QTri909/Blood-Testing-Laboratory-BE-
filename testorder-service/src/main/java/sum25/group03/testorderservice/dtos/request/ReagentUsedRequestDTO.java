package sum25.group03.testorderservice.dtos.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUsedRequestDTO {
    @NotNull(message = "Reagent ID cannot be null")
    private Long reagentId;

    private String slotNumber;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}