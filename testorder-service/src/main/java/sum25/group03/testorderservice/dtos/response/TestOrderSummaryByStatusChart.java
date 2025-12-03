package sum25.group03.testorderservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import sum25.group03.testorderservice.enums.TestOrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestOrderSummaryByStatusChart {

    private LocalDate date;              // day of the orders
    private TestOrderStatus status;      // the status
    private Long totalOrders;            // count of orders for this day + status

    public TestOrderSummaryByStatusChart(Date sqlDate,
                                         TestOrderStatus status,
                                         Long totalOrders) {
        this.date = sqlDate.toLocalDate(); // convert java.sql.Date → java.time.LocalDate
        this.status = status;
        this.totalOrders = totalOrders;
    }
}