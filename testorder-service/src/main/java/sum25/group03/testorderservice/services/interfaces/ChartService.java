package sum25.group03.testorderservice.services.interfaces;

import sum25.group03.testorderservice.dtos.response.TestOrderSummaryByStatusChart;
import sum25.group03.testorderservice.dtos.response.TestOrderSummaryChart;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    List<TestOrderSummaryChart> getTestOrdersSummary(LocalDate fromDate, LocalDate toDate);
    List<TestOrderSummaryByStatusChart> getTestOrdersSummaryByStatus(LocalDate fromDate, LocalDate toDate);
}
