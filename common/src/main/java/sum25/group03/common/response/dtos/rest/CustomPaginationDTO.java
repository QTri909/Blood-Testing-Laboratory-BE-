package sum25.group03.common.response.dtos.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomPaginationDTO<T> implements Serializable {
    private Long total;
    private Integer page;
    private Integer size;
    private T data;
}
