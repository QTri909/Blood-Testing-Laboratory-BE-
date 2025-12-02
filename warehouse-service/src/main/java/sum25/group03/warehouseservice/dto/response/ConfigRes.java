package sum25.group03.warehouseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigRes {
    private Long configurationId;
    private String configurationName;
    private String supportedTests;
    private String dataOutputFormat;
    private String communicationProtocol;
    private int mixingSpeed;
    private String firmwareVersion;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate updatedAt;
    private int createdBy;
    private int updatedBy;
    private String instrumentName;
}
