package sum25.group03.warehouseservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import sum25.group03.warehouseservice.entity.enums.ConfigType;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfigRes {
    private SpecificConfigRes specificConfigRes;
}
