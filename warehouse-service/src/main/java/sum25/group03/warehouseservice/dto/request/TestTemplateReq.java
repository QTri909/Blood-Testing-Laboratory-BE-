package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.TestType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestTemplateReq {
    @NotNull
    private TestType testType;
    @NotNull
    private List<Long> id;
    private String description;
}
