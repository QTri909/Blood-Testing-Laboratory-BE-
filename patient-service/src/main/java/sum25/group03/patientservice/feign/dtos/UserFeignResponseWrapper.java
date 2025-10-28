package sum25.group03.patientservice.feign.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeignResponseWrapper {
    private List<UserDTO> content;
    private Boolean isLast;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
}