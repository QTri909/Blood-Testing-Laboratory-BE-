package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReagentRes {
    private Long reagentId;
    private String reagentName;
    private String catalogNumber;
    private String casNumber;
    private String unit;
    private Integer quantity;
    private String storageConditions;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
}
