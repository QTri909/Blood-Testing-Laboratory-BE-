package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sum25.group03.testorderservice.enums.TestOrderType;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestOrderSummaryChart {
    private Date date;
    private Long totalTestOrders;
    private TestOrderType testOrderType;
}
