package sum25.group03.instrumentservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentPageResponse {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<InstrumentResponse> instruments;
}