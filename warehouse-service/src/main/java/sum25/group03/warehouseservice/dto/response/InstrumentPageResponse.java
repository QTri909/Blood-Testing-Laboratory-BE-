package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentPageResponse {
    private List<InstrumentResponse> content;
    private long totalElements;
    private int totalPages;
    private String message;
}
