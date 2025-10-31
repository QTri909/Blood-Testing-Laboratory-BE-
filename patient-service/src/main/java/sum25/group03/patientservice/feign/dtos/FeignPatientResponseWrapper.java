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
    private List<FeignPatientDTO> content;
    private Boolean last;
    private Boolean first;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;

    @Override
    public String toString() {
        return """
                FeignPatientResponseWrapper{
                    content=%s,
                    last=%s,
                    first=%s,
                    totalElements=%s,
                    totalPages=%s,
                    size=%s,
                    number=%s
                }
                """.formatted(
                content, last, first, totalElements, totalPages, size, number);
    }
}
