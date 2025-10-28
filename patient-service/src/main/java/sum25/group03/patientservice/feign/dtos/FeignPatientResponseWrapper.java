package sum25.group03.patientservice.feign.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeignPatientResponseWrapper implements Serializable {
    private List<FeignPatientDTO> patients;
    private Boolean last;
    private Boolean first;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
}
