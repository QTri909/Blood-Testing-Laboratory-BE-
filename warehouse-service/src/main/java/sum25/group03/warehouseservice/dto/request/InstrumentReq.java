package sum25.group03.warehouseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
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
    private String model;
    @NotBlank
    private String serialNumber;
    private String location;
    private LocalDate installationDate;
    private LocalDate lastCalibrationDate;
    private LocalDate nextCalibrationDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String notes;
    @NotBlank
    private String manufacturer;
    private Long cloneFromInstrumentId;
}
