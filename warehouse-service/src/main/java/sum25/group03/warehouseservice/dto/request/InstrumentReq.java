package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InstrumentReq {
    @NotBlank
    private String instrumentName;
    @NotBlank
    private String instrumentCode;
    @NotBlank
    private String instrumentType;
    @NotBlank
    private String serialNumber;
    @NotBlank
    private String firmwareVersion;
    @NotBlank
    private String warrantyExpiryDate;
    private String installationDate;
    private String location;
    private String notes;
    @NotBlank
    private ManufacturerReq manufacturer;
    private Long configurationId;
    private List<Long> reagentId;
    private Long cloneFromInstrumentId;
}
