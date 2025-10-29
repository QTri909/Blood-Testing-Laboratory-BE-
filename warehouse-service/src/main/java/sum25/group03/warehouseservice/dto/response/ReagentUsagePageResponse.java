package sum25.group03.warehouseservice.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentUsagePageResponse {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<ReagentUsageDetailResponse> usages;
}
