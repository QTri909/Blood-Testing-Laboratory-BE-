package sum25.group03.patientservice.feign.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeignUserResponseWrapper implements Serializable {
    private List<FeignUserDTO> content;
    private Boolean last;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;

    @Override
    public String toString() {
        return """
                UserFeignResponseWrapper{
                    content=%s,
                    last=%s,
                    totalElements=%s,
                    totalPages=%s,
                    size=%s,
                    number=%s
                }
                """.formatted(
                content, last, totalElements, totalPages, size, number);
    }
}